package com.benewake.system.service.scheduling.kingdee.impl;

import com.benewake.system.entity.ApsMaterial;
import com.benewake.system.entity.ApsOrder;
import com.benewake.system.entity.ApsOutsourcedOrder;
import com.benewake.system.entity.enums.FPickMtrlStatusEnum;
import com.benewake.system.entity.enums.FStatusEnum;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedOrder;
import com.benewake.system.entity.kingdee.transfer.fLotIdToFNumber;
import com.benewake.system.mapper.ApsOutsourcedOrderMapper;
import com.benewake.system.service.scheduling.kingdee.ApsOrderBaseService;
import com.benewake.system.service.scheduling.kingdee.ApsOutsourcedOrderService;
import com.benewake.system.transfer.OutsourcedKingdeeToApsOrder;
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
public class ApsOutsourcedOrderServiceImpl extends ApsOrderBaseService implements ApsOutsourcedOrderService {

    @Autowired
    private K3CloudApi api;

    @Autowired
    private OutsourcedKingdeeToApsOrder outsourcedKingdeeToApsOrder;

    @Override
    public String getServiceName() {
        return "委外订单列表";
    }

    @Override
    public List<ApsOrder> getKingdeeDates() throws Exception {
        List<KingdeeOutsourcedOrder> result = getKingdeeOutsourcedOrders();
        // 物料映射表
        Map<String, String> mtn = getMaterialIdToNameMap();
        //单据映射表
        Map<String, String> ftn = getBillTypeIdToNameMap();

        Map<String, String> lotIdToFNumberMap = getLotIdToFNumberMap();
        //BOM版本号映射表
        Map<String, String> btn = getFIDToNumberMap();
        //转换

        return getApsOutsourcedOrders(result, mtn, ftn, btn ,lotIdToFNumberMap);
    }

    public Map<String, String> getLotIdToFNumberMap() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("BD_BatchMainFile");
        queryParam.setFieldKeys("FLOTID,FNumber");
        List<fLotIdToFNumber> fLotIdToFNumbers = api.executeBillQuery(queryParam, fLotIdToFNumber.class);
        Map<String, String> fLotIdToFNumberMap = new HashMap<>();
        fLotIdToFNumbers.forEach(fLotIdToFNumber -> {
            fLotIdToFNumberMap.put(fLotIdToFNumber.getFLOTID(), fLotIdToFNumber.getFNumber());
        });
        return fLotIdToFNumberMap;
    }


    private List<ApsOrder> getApsOutsourcedOrders(List<KingdeeOutsourcedOrder> result, Map<String, String> mtn, Map<String, String> ftn, Map<String, String> btn, Map<String, String> lotIdToFNumberMap) throws NoSuchFieldException, IllegalAccessException {
        List<ApsOrder> apsOrders = new ArrayList<>();

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
            kingdeeOutsourcedOrder.setF_ora_FDZMaterialID2(kingdeeOutsourcedOrder.getF_ora_FDZMaterialID2());
            String fLot = kingdeeOutsourcedOrder.getFLot();
            kingdeeOutsourcedOrder.setFLot(lotIdToFNumberMap.getOrDefault(fLot ,fLot + "映射表内不存在----"));
            ApsOrder apsOrder = outsourcedKingdeeToApsOrder.convert(kingdeeOutsourcedOrder);
            apsOrders.add(apsOrder);
        }
        return apsOrders;
    }

    private List<KingdeeOutsourcedOrder> getKingdeeOutsourcedOrders() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("SUB_SUBREQORDER");
        queryParam.setFieldKeys("FBillNo,FBillType,FMaterialId,FMaterialName,FQty,FLot,FStatus,FPickMtrlStatus,FStockInQty,FBomId,F_ora_FDZMaterialID2");
        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FDocumentStatus = 'C'");     // 单据状态=已审核
        queryFilters.add("FCancelStatus = 'A'");       // 作废状态=未作废
        queryFilters.add("FStatus != '6'");              // 业务状态≠结案
        queryFilters.add("FStatus != '7'");
        queryParam.setFilterString(String.join(" and ", queryFilters));
//        queryParam.setLimit(100);
        return api.executeBillQuery(queryParam, KingdeeOutsourcedOrder.class);
    }

}




