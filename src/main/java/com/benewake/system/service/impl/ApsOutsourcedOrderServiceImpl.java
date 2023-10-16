package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsOutsourcedOrder;
import com.benewake.system.entity.Interface.ApsOutsourcedMaterialMultipleVersions;
import com.benewake.system.entity.Interface.ApsOutsourcedOrderMultipleVersions;
import com.benewake.system.entity.enums.FPickMtrlStatusEnum;
import com.benewake.system.entity.enums.FStatusEnum;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedOrder;
import com.benewake.system.entity.kingdee.transfer.FBILLTYPEIDToName;
import com.benewake.system.entity.kingdee.transfer.FIDToNumber;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.service.ApsOutsourcedOrderService;
import com.benewake.system.mapper.ApsOutsourcedOrderMapper;
import com.benewake.system.transfer.kingdeeToApsOutsourcedOrder;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author ASUS
* @description 针对表【aps_outsourced_order】的数据库操作Service实现
* @createDate 2023-10-07 17:35:47
*/
@Service
public class ApsOutsourcedOrderServiceImpl extends ServiceImpl<ApsOutsourcedOrderMapper, ApsOutsourcedOrder>
    implements ApsOutsourcedOrderService{

    @Autowired
    private K3CloudApi api;

    @Autowired
    private kingdeeToApsOutsourcedOrder kingdeeToApsOutsourcedOrder;

    @Autowired
    private ApsOutsourcedOrderMapper apsOutsourcedOrderMapper;

    @Override
    public Boolean updateDataVersions() throws Exception {
        List<KingdeeOutsourcedOrder> result = getKingdeeOutsourcedOrders();
        // 物料映射表
        Map<String, String> mtn = getMaterialIdToNameMap();
        //单据映射表
        Map<String, String> ftn = getFBILLTYPEIDToNameMap();
        //BOM版本号映射表
        Map<String, String> btn = getFIDToNumberMap();
        //转换
        ArrayList<ApsOutsourcedOrder> apsOutsourcedOrders = getApsOutsourcedOrders(result, mtn, ftn, btn);
        return saveBatch(apsOutsourcedOrders);
    }

    @Override
    public List<Object> selectVersionPageList(Integer pass, Integer size, List versionToChVersionArrayList) {
        return (List<Object>) apsOutsourcedOrderMapper.selectVersionPageList(pass, size, versionToChVersionArrayList);
    }

    private ArrayList<ApsOutsourcedOrder> getApsOutsourcedOrders(List<KingdeeOutsourcedOrder> result, Map<String, String> mtn, Map<String, String> ftn, Map<String, String> btn) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<ApsOutsourcedOrder> apsOutsourcedOrders = new ArrayList<>();
        Integer maxVersion = this.getMaxVersionIncr();
        for (KingdeeOutsourcedOrder kingdeeOutsourcedOrder : result) {
            // 获取 FStatus 的 id
            String statusId = kingdeeOutsourcedOrder.getFStatus();
            // 使用映射 HashMap 获取状态文字
            String statusText = FStatusEnum.getByCode(statusId).getDescription();
            if (statusText != null) {
                kingdeeOutsourcedOrder.setFStatus(statusText);
            } else {
                kingdeeOutsourcedOrder.setFStatus("未知状态"); // 或者其他默认值
            }
            // 获取 FPickMtrlStatus 的 id
            String pickStatusId = kingdeeOutsourcedOrder.getFPickMtrlStatus();
            // 使用映射 HashMap 获取状态文字
            String pickStatusText = FPickMtrlStatusEnum.getByCode(pickStatusId).getDescription();
            if (pickStatusText != null) {
                kingdeeOutsourcedOrder.setFPickMtrlStatus(pickStatusText);
            } else {
                kingdeeOutsourcedOrder.setFPickMtrlStatus("未知状态"); // 或者其他默认值
            }
            kingdeeOutsourcedOrder.setFMaterialId(mtn.get(kingdeeOutsourcedOrder.getFMaterialId()));
            kingdeeOutsourcedOrder.setFBomId(btn.get(kingdeeOutsourcedOrder.getFBomId()));
            String originalFBillType = kingdeeOutsourcedOrder.getFBillType();
            kingdeeOutsourcedOrder.setFBillType(ftn.get(originalFBillType));
            ApsOutsourcedOrder apsOutsourcedOrder = kingdeeToApsOutsourcedOrder.convert(kingdeeOutsourcedOrder , maxVersion);
            apsOutsourcedOrders.add(apsOutsourcedOrder);
        }
        return apsOutsourcedOrders;
    }

    private Map<String, String> getFIDToNumberMap() throws Exception {
        QueryParam queryParam;
        queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FID,FNumber");
        List<FIDToNumber> bidToName = api.executeBillQuery(queryParam, FIDToNumber.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String, String> btn = new HashMap<>();
        bidToName.forEach(c -> {//遍历midtoname列表并且对列表中的数据做括号内的操作
            btn.put(c.getFID(), c.getFNumber());//将物料的id作为键，物料编号作为值，将键值对添加到映射表中
        });
        return btn;
    }

    private Map<String, String> getFBILLTYPEIDToNameMap() throws Exception {
        QueryParam queryParam;
        queryParam = new QueryParam();
        queryParam.setFormId("BOS_BillType");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FBILLTYPEID,FName");
        List<FBILLTYPEIDToName> fidToName = api.executeBillQuery(queryParam, FBILLTYPEIDToName.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String, String> ftn = new HashMap<>();
        fidToName.forEach(c -> {//遍历midtoname列表并且对列表中的数据做括号内的操作
            ftn.put(c.getFBILLTYPEID(), c.getFName());//将物料的id作为键，物料编号作为值，将键值对添加到映射表中
        });
        return ftn;
    }

    private Map<String, String> getMaterialIdToNameMap() throws Exception {
        QueryParam queryParam;
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> mtn = new HashMap<>();
        midToName.forEach(c -> {
            mtn.put(c.getFMaterialId(), c.getFNumber());
        });
        return mtn;
    }

    private List<KingdeeOutsourcedOrder> getKingdeeOutsourcedOrders() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("SUB_SUBREQORDER");
        queryParam.setFieldKeys("FBillNo,FBillType,FMaterialId,FQty,FStatus,FPickMtrlStatus,FStockInQty,FBomId");
        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FDocumentStatus = 'C'");     // 单据状态=已审核
        queryFilters.add("FCancelStatus = 'A'");       // 作废状态=未作废
        queryFilters.add("FStatus != '6'");              // 业务状态≠结案
        queryFilters.add("FStatus != '7'");
        queryParam.setFilterString(String.join(" and ", queryFilters));
//        queryParam.setLimit(100);
        List<KingdeeOutsourcedOrder> result = api.executeBillQuery(queryParam, KingdeeOutsourcedOrder.class);
        return result;
    }

}




