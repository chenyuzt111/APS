package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPurchaseRequest;
import com.benewake.system.entity.ApsReceiveNotice;
import com.benewake.system.entity.kingdee.KingdeePurchaseRequest;
import com.benewake.system.entity.kingdee.KingdeeReceiveNotice;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsReceiveNotice {

    public ApsReceiveNotice convert(KingdeeReceiveNotice kingdeeReceiveNotice , Integer version) {
        if (kingdeeReceiveNotice == null || version == null) {
            return null;
        }
        ApsReceiveNotice apsReceiveNotice = new ApsReceiveNotice();
        apsReceiveNotice.setFBillNo(kingdeeReceiveNotice.getFBillNo());
        apsReceiveNotice.setFMaterialId(kingdeeReceiveNotice.getFMaterialId());
        apsReceiveNotice.setFMustQty(kingdeeReceiveNotice.getFMustQty());
        apsReceiveNotice.setFCheckQty(kingdeeReceiveNotice.getFCheckQty());
        apsReceiveNotice.setFReceiveQty(kingdeeReceiveNotice.getFReceiveQty());
        apsReceiveNotice.setFCsnReceiveBaseQty(kingdeeReceiveNotice.getFCsnReceiveBaseQty());
        apsReceiveNotice.setFInStockQty(kingdeeReceiveNotice.getFInStockQty());
        apsReceiveNotice.setFMaterialName(kingdeeReceiveNotice.getFMaterialName());
        apsReceiveNotice.setVersion(version);
        return apsReceiveNotice;
    }
}
