package com.benewake.system.transfer;

import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.kingdee.KingdeeImmediatelyInventory;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsImmediatelyInventory {

    public ApsImmediatelyInventory convert(KingdeeImmediatelyInventory object ,Integer version) {
        if (object == null || version == null) {
            return null;
        }
        ApsImmediatelyInventory apsImmediatelyInventory = new ApsImmediatelyInventory();
        apsImmediatelyInventory.setFAvbQty(object.getFAVBQty());
        apsImmediatelyInventory.setFLot(object.getFLot());
        apsImmediatelyInventory.setFExpiryDate(object.getFExpiryDate());
        apsImmediatelyInventory.setFMaterialId(object.getFMaterialId());
        apsImmediatelyInventory.setFStockName(object.getFStockName());
        apsImmediatelyInventory.setVersion(version);

        return apsImmediatelyInventory;
    }




}
