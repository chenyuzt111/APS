package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPurchaseOrder;
import com.benewake.system.entity.ApsPurchaseRequestsOrders;
import com.benewake.system.entity.kingdee.KingdeePurchaseOrder;
import com.benewake.system.utils.BenewakeStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class KingdeeToApsPurchaseOrder {

    public ApsPurchaseRequestsOrders convert(KingdeePurchaseOrder purchaseOrder) throws ParseException {
        if (purchaseOrder == null) {
            return null;
        }
        ApsPurchaseRequestsOrders purchaseRequestsOrders = new ApsPurchaseRequestsOrders();
        purchaseRequestsOrders.setBillNo(purchaseOrder.getFBillNo());
        purchaseRequestsOrders.setMaterialId(purchaseOrder.getFMaterialId());
        purchaseRequestsOrders.setMaterialName(purchaseOrder.getFMaterialName());
        purchaseRequestsOrders.setBaseUnitQty(purchaseOrder.getFRemainReceiveQty());
        String fDeliveryDate_plan = purchaseOrder.getFDeliveryDate_Plan();
        if (StringUtils.isNotEmpty(fDeliveryDate_plan)) {
            Date parse = BenewakeStringUtils.parse(fDeliveryDate_plan, "yyyy-MM-dd'T'HH:mm:ss");
            purchaseRequestsOrders.setArrivalDate(parse);
        }
        purchaseRequestsOrders.setFormName("采购订单列表");

        return purchaseRequestsOrders;
    }
}
