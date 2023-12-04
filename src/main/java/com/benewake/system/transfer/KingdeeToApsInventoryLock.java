package com.benewake.system.transfer;

import com.benewake.system.entity.ApsInventoryLock;
import com.benewake.system.entity.kingdee.KingdeeInventoryLock;
import com.benewake.system.utils.BenewakeStringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class KingdeeToApsInventoryLock {
    public ApsInventoryLock convert(KingdeeInventoryLock kingdeeInventoryLock , Integer version) throws ParseException {
        ApsInventoryLock apsInventoryLock = new ApsInventoryLock();
        apsInventoryLock.setFMaterialId(kingdeeInventoryLock.getFMaterialId());
        Date parse = BenewakeStringUtils.parse(kingdeeInventoryLock.getFEXPIRYDATE(), "yyyy-MM-dd'T'HH:mm:ss");
        apsInventoryLock.setFExpiryDate(parse);
        apsInventoryLock.setFLockQty(kingdeeInventoryLock.getFLockQty());
        apsInventoryLock.setFLot(kingdeeInventoryLock.getFLot());
//        apsInventoryLock.setFMaterialName(kingdeeInventoryLock.getFMaterialName());
        apsInventoryLock.setVersion(version);

        return apsInventoryLock;
    }
}
