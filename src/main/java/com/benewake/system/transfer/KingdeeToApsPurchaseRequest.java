package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPurchaseRequest;
import com.benewake.system.entity.kingdee.KingdeePurchaseRequest;
import com.benewake.system.utils.BenewakeStringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class KingdeeToApsPurchaseRequest {
    public ApsPurchaseRequest convert(KingdeePurchaseRequest kingdeePurchaseRequest , Integer version) throws ParseException {
        if (kingdeePurchaseRequest == null || version == null) {
            return null;
        }
        ApsPurchaseRequest apsPurchaseRequest = new ApsPurchaseRequest();
        apsPurchaseRequest.setFMaterialId(kingdeePurchaseRequest.getFMaterialId());
        apsPurchaseRequest.setFBaseUnitQty(kingdeePurchaseRequest.getFBaseUnitQty());
        Date parse = BenewakeStringUtils.parse(kingdeePurchaseRequest.getFArrivalDate(), "yyyy-MM-dd'T'HH:mm:ss");
        apsPurchaseRequest.setFArrivalDate(parse);
//        apsPurchaseRequest.setFMaterialName(kingdeePurchaseRequest.getFMaterialName());
        apsPurchaseRequest.setVersion(version);

        return apsPurchaseRequest;
    }
}
