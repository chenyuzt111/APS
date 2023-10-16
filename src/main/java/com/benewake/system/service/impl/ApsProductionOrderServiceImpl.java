package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProductionOrder;
import com.benewake.system.entity.enums.FPickMtrlStatusEnum;
import com.benewake.system.entity.enums.FStatusEnum;
import com.benewake.system.entity.kingdee.KingdeeProductionOrder;
import com.benewake.system.entity.kingdee.transfer.FBILLTYPEIDToName;
import com.benewake.system.entity.kingdee.transfer.FIDToNumber;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.service.ApsProductionOrderService;
import com.benewake.system.mapper.ApsProductionOrderMapper;
import com.benewake.system.transfer.KingdeeToApsProductionOrder;
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
* @description 针对表【aps_production_order】的数据库操作Service实现
* @createDate 2023-10-07 18:22:11
*/
@Service
public class ApsProductionOrderServiceImpl extends ServiceImpl<ApsProductionOrderMapper, ApsProductionOrder>
    implements ApsProductionOrderService{

    @Autowired
    private K3CloudApi api;

    @Autowired
    private KingdeeToApsProductionOrder kingdeeToApsProductionOrder;

    @Autowired
    private ApsProductionOrderMapper apsProductionOrderMapper;
    @Override
    public Boolean updateDataVersions() throws Exception {

        List<KingdeeProductionOrder> result = getKingdeeProductionOrders();
        // 物料映射表
        Map<String, String> mtn = getMaterialIdToNameMap();
        //单据映射表
        Map<String, String> ftn = getFBILLTYPEIDToNameMap();
        //BOM版本号映射表
        Map<String, String> btn = getFIDToNumberMap();

        ArrayList<ApsProductionOrder> apsProductionOrders = new ArrayList<>();
        Integer maxVersion = this.getMaxVersionIncr();
        for (KingdeeProductionOrder kingdeeProductionOrder : result) {
            getApsProductionOrderList(maxVersion ,mtn, ftn, btn, apsProductionOrders, kingdeeProductionOrder);
        }

        return saveBatch(apsProductionOrders);
    }

    @Override
    public List<Object> selectVersionPageList(Integer pass, Integer size, List versionToChVersionArrayList) {
        return (List<Object>) apsProductionOrderMapper.selectVersionPageList(pass, size, versionToChVersionArrayList);
    }

    private void getApsProductionOrderList(Integer maxVersion, Map<String, String> mtn, Map<String, String> ftn, Map<String, String> btn, ArrayList<ApsProductionOrder> apsProductionOrders, KingdeeProductionOrder kingdeeProductionOrder) {
        // 获取 FStatus 的 id
        String statusId = kingdeeProductionOrder.getFStatus();
        // 使用映射 HashMap 获取状态文字
        FStatusEnum fStatusEnum = FStatusEnum.getByCode(statusId);
        //存在枚举值不存在的情况
        String statusText = fStatusEnum != null? fStatusEnum.getDescription() : statusId;
        if (statusText != null) {
            kingdeeProductionOrder.setFStatus(statusText);
        }
        // 获取 FPickMtrlStatus 的 id
        String pickStatusId = kingdeeProductionOrder.getFPickMtrlStatus();
        // 使用映射 HashMap 获取状态文字
        String pickStatusText = FPickMtrlStatusEnum.getByCode(pickStatusId).getDescription();
        if (pickStatusText != null) {
            kingdeeProductionOrder.setFPickMtrlStatus(pickStatusText);
        }
        kingdeeProductionOrder.setFMaterialId(mtn.get(kingdeeProductionOrder.getFMaterialId()));
        String originalFBillType = kingdeeProductionOrder.getFBillType();
        kingdeeProductionOrder.setFBillType(ftn.get(originalFBillType));
        kingdeeProductionOrder.setFBomId(btn.get(kingdeeProductionOrder.getFBomId()));
        ApsProductionOrder apsProductionOrder = kingdeeToApsProductionOrder.convert(kingdeeProductionOrder, maxVersion);
        apsProductionOrders.add(apsProductionOrder);
    }

    private Map<String, String> getFIDToNumberMap() throws Exception {
        QueryParam queryParam = new QueryParam();
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
        QueryParam queryParam = new QueryParam();
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
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> mtn = new HashMap<>();
        midToName.forEach(c -> {
            mtn.put(c.getFMaterialId(), c.getFNumber());
        });
        return mtn;
    }

    private List<KingdeeProductionOrder> getKingdeeProductionOrders() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("PRD_MO");
        queryParam.setFieldKeys("FStatus,FWorkshopID,FBillNo,FBillType,FMaterialId,FQty,FStatus,FPickMtrlStatus,FStockInQuaAuxQty,FBomId,FWorkShopID");
        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FDocumentStatus = 'C'");     // 单据状态=已审核
        queryFilters.add("FCancelStatus = 'A'");       // 作废状态=未作废
        queryFilters.add("FStatus != '6'");              // 业务状态≠结案
        queryFilters.add("FStatus != '7'");
        queryFilters.add("FWorkshopID =102714");

        queryParam.setFilterString(String.join(" and ", queryFilters));

        List<KingdeeProductionOrder> result = api.executeBillQuery(queryParam, KingdeeProductionOrder.class);
        return result;
    }
}




