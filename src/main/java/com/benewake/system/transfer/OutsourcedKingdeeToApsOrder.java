package com.benewake.system.transfer;

import com.benewake.system.entity.ApsOrder;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedOrder;
import org.springframework.stereotype.Component;

@Component
public class OutsourcedKingdeeToApsOrder {

    public ApsOrder convert(KingdeeOutsourcedOrder kingdeeOutsourcedOrder) {
        if (kingdeeOutsourcedOrder == null) {
            return null;
        }
        ApsOrder apsOrder = new ApsOrder();
        apsOrder.setBillNo(kingdeeOutsourcedOrder.getFBillNo());
        apsOrder.setBillType(kingdeeOutsourcedOrder.getFBillType());
        apsOrder.setMaterialId(kingdeeOutsourcedOrder.getFMaterialId());
        apsOrder.setQty(kingdeeOutsourcedOrder.getFQty());
        apsOrder.setStatus(kingdeeOutsourcedOrder.getFStatus());
        apsOrder.setPickMtrlStatus(kingdeeOutsourcedOrder.getFPickMtrlStatus());
        apsOrder.setStockInQuaAuxQty(kingdeeOutsourcedOrder.getFStockInQty());
        apsOrder.setBomId(kingdeeOutsourcedOrder.getFBomId());
        apsOrder.setFormName("委外订单列表");

        return apsOrder;
    }

}
