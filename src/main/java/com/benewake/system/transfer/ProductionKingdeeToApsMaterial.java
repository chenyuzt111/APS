package com.benewake.system.transfer;

import com.benewake.system.entity.ApsMaterial;
import com.benewake.system.entity.kingdee.KingdeeProductionMaterial;
import org.springframework.stereotype.Component;

@Component
public class ProductionKingdeeToApsMaterial {

    public ApsMaterial convert(KingdeeProductionMaterial kingdeeProductionMaterial ) {
        if (kingdeeProductionMaterial == null) {
            return null;
        }
        ApsMaterial apsMaterial = new ApsMaterial();
        apsMaterial.setMaterialId(kingdeeProductionMaterial.getFMaterialID());
        apsMaterial.setSubReqBillNo(kingdeeProductionMaterial.getFMOBillNO());
        apsMaterial.setMaterialId2(kingdeeProductionMaterial.getFMaterialID2());
        apsMaterial.setMaterialType(kingdeeProductionMaterial.getFMaterialType());
        apsMaterial.setMustQty(kingdeeProductionMaterial.getFMustQty());
        apsMaterial.setPickedQty(kingdeeProductionMaterial.getFPickedQty());
        apsMaterial.setGoodReturnQty(kingdeeProductionMaterial.getFGoodReturnQty());
        apsMaterial.setProcessDefectReturnQty(kingdeeProductionMaterial.getFProcessDefectReturnQty());
        apsMaterial.setFormName("生产用料清单列表");
        apsMaterial.setVersion(null);

        return apsMaterial;
    }

}
