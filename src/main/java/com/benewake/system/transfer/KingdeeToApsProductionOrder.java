package com.benewake.system.transfer;

import com.benewake.system.entity.ApsProductionOrder;
import com.benewake.system.entity.kingdee.KingdeeProductionOrder;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsProductionOrder {

    public ApsProductionOrder convert(KingdeeProductionOrder kingdeeProductionOrder , Integer version) {
        if (kingdeeProductionOrder == null || version == null) {
            return null;
        }
        ApsProductionOrder apsProductionOrder = new ApsProductionOrder();
        apsProductionOrder.setFBillNo(kingdeeProductionOrder.getFBillNo());
        apsProductionOrder.setFBillType(kingdeeProductionOrder.getFBillType());
        apsProductionOrder.setFBillTypeId(kingdeeProductionOrder.getFBillType());
        apsProductionOrder.setFMaterialId(kingdeeProductionOrder.getFMaterialId());
        apsProductionOrder.setFQty(kingdeeProductionOrder.getFQty());
        apsProductionOrder.setFStatus(kingdeeProductionOrder.getFStatus());
        apsProductionOrder.setFPickMtrlStatus(kingdeeProductionOrder.getFPickMtrlStatus());
        apsProductionOrder.setFStockInQuaAuxQty(kingdeeProductionOrder.getFStockInQuaAuxQty());
        apsProductionOrder.setFBomId(kingdeeProductionOrder.getFBomId());
        apsProductionOrder.setFMaterialName(kingdeeProductionOrder.getFMaterialName());
        apsProductionOrder.setVersion(version);

        return apsProductionOrder;
    }

}
