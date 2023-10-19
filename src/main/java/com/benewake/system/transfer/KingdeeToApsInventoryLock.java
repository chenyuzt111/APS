package com.benewake.system.transfer;

import com.benewake.system.entity.ApsInventoryLock;
import com.benewake.system.entity.ApsOutsourcedMaterial;
import com.benewake.system.entity.kingdee.KingdeeInventoryLock;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedMaterial;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsInventoryLock {
    public ApsInventoryLock convert(KingdeeInventoryLock kingdeeInventoryLock , Integer version) {
        ApsInventoryLock apsInventoryLock = new ApsInventoryLock();
        apsInventoryLock.setFMaterialId(kingdeeInventoryLock.getFMaterialId());
        apsInventoryLock.setFExpiryDate(kingdeeInventoryLock.getFEXPIRYDATE());
        apsInventoryLock.setFLockQty(kingdeeInventoryLock.getFLockQty());
        apsInventoryLock.setFLot(kingdeeInventoryLock.getFLot());
        apsInventoryLock.setFMaterialName(kingdeeInventoryLock.getFMaterialName());
        apsInventoryLock.setVersion(version);

        return apsInventoryLock;
    }
}
