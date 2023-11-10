package com.benewake.system.transfer;

import com.benewake.system.entity.ApsProductionOrder;
import com.benewake.system.entity.ApsPurchaseOrder;
import com.benewake.system.entity.kingdee.KingdeeProductionOrder;
import com.benewake.system.entity.kingdee.KingdeePurchaseOrder;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsPurchaseOrder {

    public ApsPurchaseOrder convert(KingdeePurchaseOrder purchaseOrder , Integer version) {
        if (purchaseOrder == null || version == null) {
            return null;
        }
        ApsPurchaseOrder apsPurchaseOrder = new ApsPurchaseOrder();
        apsPurchaseOrder.setFBillNo(purchaseOrder.getFBillNo());
        apsPurchaseOrder.setFMaterialId(purchaseOrder.getFMaterialId());
//        apsPurchaseOrder.setFMaterialName(purchaseOrder.getFMaterialName());
        apsPurchaseOrder.setFRemainReceiveQty(purchaseOrder.getFDeliRemainQty());
        apsPurchaseOrder.setFDeliveryDate(purchaseOrder.getFDeliveryDate_Plan());
        apsPurchaseOrder.setVersion(version);

        return apsPurchaseOrder;
    }
}
