package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsOrder;
import com.benewake.system.entity.dto.ApsProductionOrderDto;
import com.benewake.system.entity.enums.FPickMtrlStatusEnum;
import com.benewake.system.entity.enums.FStatusEnum;
import com.benewake.system.entity.kingdee.KingdeeProductionOrder;
import com.benewake.system.mapper.ApsProductionOrderMapper;
import com.benewake.system.service.scheduling.kingdee.ApsOrderBaseService;
import com.benewake.system.service.scheduling.kingdee.ApsProductionOrderService;
import com.benewake.system.transfer.ProductionKingdeeToApsOrder;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
public class ApsProductionOrderServiceImpl extends ApsOrderBaseService
        implements ApsProductionOrderService {

    @Autowired
    private K3CloudApi api;

    @Autowired
    private ProductionKingdeeToApsOrder productionKingdeeToApsOrder;

    @Override
    public List<ApsOrder> getKingdeeDates() throws Exception {
        List<KingdeeProductionOrder> result = getKingdeeProductionOrders();

        // 物料映射表
        Map<String, String> mtn = getMaterialIdToNameMap();
        //单据映射表
        Map<String, String> ftn = getBillTypeIdToNameMap();
        //BOM版本号映射表
        Map<String, String> btn = getFIDToNumberMap();

        List<ApsOrder> apsOrders = new ArrayList<>();
        for (KingdeeProductionOrder kingdeeProductionOrder : result) {
            getApsProductionOrderList(mtn, ftn, btn, apsOrders, kingdeeProductionOrder);
        }

        return apsOrders;
    }

    private void getApsProductionOrderList(Map<String, String> mtn, Map<String, String> ftn, Map<String, String> btn, List<ApsOrder> apsProductionOrders, KingdeeProductionOrder kingdeeProductionOrder) throws ParseException {
        // 获取 FStatus 的 id
        String statusId = kingdeeProductionOrder.getFStatus();
        // 使用映射 HashMap 获取状态文字
        FStatusEnum fStatusEnum = FStatusEnum.getByCode(statusId);
        //存在枚举值不存在的情况
        String statusText = fStatusEnum != null ? fStatusEnum.getDescription() : statusId;
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
        kingdeeProductionOrder.setFStatus(projectStatusMap.get(kingdeeProductionOrder.getFStatus()));
        ApsOrder apsOrder = productionKingdeeToApsOrder.convert(kingdeeProductionOrder);
        apsProductionOrders.add(apsOrder);
    }


    private List<KingdeeProductionOrder> getKingdeeProductionOrders() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("PRD_MO");
        queryParam.setFieldKeys("FStatus,FWorkshopID,FBillNo,FBillType,FMaterialId,FMaterialName,FQty,FStatus,FPickMtrlStatus,FStockInQuaAuxQty,FPlanFinishDate,FBomId,FWorkShopID,F_ora_FDZMaterialID2");
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




