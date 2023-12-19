package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPurchaseRequest;
import com.benewake.system.entity.ApsPurchaseRequestsOrders;
import com.benewake.system.entity.kingdee.KingdeePurchaseRequest;
import com.benewake.system.utils.BenewakeStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class KingdeeToApsPurchaseRequest {
    public ApsPurchaseRequestsOrders convert(KingdeePurchaseRequest kingdeePurchaseRequest) throws ParseException {
        if (kingdeePurchaseRequest == null ) {
            return null;
        }
        ApsPurchaseRequestsOrders purchaseRequestsOrders = new ApsPurchaseRequestsOrders();
        purchaseRequestsOrders.setBillNo(kingdeePurchaseRequest.getFBillNo());
        purchaseRequestsOrders.setMaterialId(kingdeePurchaseRequest.getFMaterialId());
        purchaseRequestsOrders.setMaterialName(kingdeePurchaseRequest.getFMaterialName());
        purchaseRequestsOrders.setBaseUnitQty(kingdeePurchaseRequest.getFBaseUnitQty());
        String fArrivalDate = kingdeePurchaseRequest.getFArrivalDate();
        if (StringUtils.isNotEmpty(fArrivalDate)) {
            Date parse = BenewakeStringUtils.parse(fArrivalDate, "yyyy-MM-dd'T'HH:mm:ss");
            purchaseRequestsOrders.setArrivalDate(parse);
        }
        purchaseRequestsOrders.setFormName("采购申请单列表");
        return purchaseRequestsOrders;
    }
}
