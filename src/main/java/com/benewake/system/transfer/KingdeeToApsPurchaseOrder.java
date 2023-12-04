package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPurchaseOrder;
import com.benewake.system.entity.kingdee.KingdeePurchaseOrder;
import com.benewake.system.utils.BenewakeStringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class KingdeeToApsPurchaseOrder {

    public ApsPurchaseOrder convert(KingdeePurchaseOrder purchaseOrder , Integer version) throws ParseException {
        if (purchaseOrder == null || version == null) {
            return null;
        }
        ApsPurchaseOrder apsPurchaseOrder = new ApsPurchaseOrder();
        apsPurchaseOrder.setFBillNo(purchaseOrder.getFBillNo());
        apsPurchaseOrder.setFMaterialId(purchaseOrder.getFMaterialId());
//        apsPurchaseOrder.setFMaterialName(purchaseOrder.getFMaterialName());
        apsPurchaseOrder.setFRemainReceiveQty(purchaseOrder.getFRemainReceiveQty());
        Date parse = BenewakeStringUtils.parse(purchaseOrder.getFDeliveryDate_Plan(), "yyyy-MM-dd'T'HH:mm:ss");
        apsPurchaseOrder.setFDeliveryDate(parse);
        apsPurchaseOrder.setVersion(version);

        return apsPurchaseOrder;
    }
}
