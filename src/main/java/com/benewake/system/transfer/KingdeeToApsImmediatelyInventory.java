package com.benewake.system.transfer;

import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.kingdee.KingdeeImmediatelyInventory;
import com.benewake.system.utils.BenewakeStringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class KingdeeToApsImmediatelyInventory {

    public ApsImmediatelyInventory convert(KingdeeImmediatelyInventory object ,Integer version) throws ParseException {
        if (object == null || version == null) {
            return null;
        }
        ApsImmediatelyInventory apsImmediatelyInventory = new ApsImmediatelyInventory();
        apsImmediatelyInventory.setFAvbQty(object.getFAVBQty());
        apsImmediatelyInventory.setFBaseQty(object.getFBaseQty());
        apsImmediatelyInventory.setFLot(object.getFLot());
        Date parse = BenewakeStringUtils.parse(object.getFExpiryDate(), "yyyy-MM-dd'T'HH:mm:ss");
        apsImmediatelyInventory.setFExpiryDate(parse);
        apsImmediatelyInventory.setFMaterialId(object.getFMaterialId());
        apsImmediatelyInventory.setFStockName(object.getFStockName());
        apsImmediatelyInventory.setVersion(version);
//        apsImmediatelyInventory.setFMaterialName(object.getFMaterialName());

        return apsImmediatelyInventory;
    }




}
