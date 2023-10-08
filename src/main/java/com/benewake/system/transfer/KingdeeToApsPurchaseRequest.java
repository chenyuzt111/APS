package com.benewake.system.transfer;

import com.benewake.system.entity.ApsProductionOrder;
import com.benewake.system.entity.ApsPurchaseRequest;
import com.benewake.system.entity.kingdee.KingdeeProductionOrder;
import com.benewake.system.entity.kingdee.KingdeePurchaseRequest;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsPurchaseRequest {
    public ApsPurchaseRequest convert(KingdeePurchaseRequest kingdeePurchaseRequest , Integer version) {
        if (kingdeePurchaseRequest == null || version == null) {
            return null;
        }
        ApsPurchaseRequest apsPurchaseRequest = new ApsPurchaseRequest();
        apsPurchaseRequest.setFMaterialId(kingdeePurchaseRequest.getFMaterialId());
        apsPurchaseRequest.setFBaseUnitQty(kingdeePurchaseRequest.getFBaseUnitQty());
        apsPurchaseRequest.setFArrivalDate(kingdeePurchaseRequest.getFArrivalDate());
        apsPurchaseRequest.setVersion(version);

        return apsPurchaseRequest;
    }
}
