package com.benewake.system.transfer;

import com.benewake.system.entity.ApsMaterial;
import com.benewake.system.entity.ApsProductionMaterial;
import com.benewake.system.entity.kingdee.KingdeeProductionMaterial;
import org.springframework.stereotype.Component;

@Component
public class ProductionKingdeeToApsMaterial {

    public ApsMaterial convert(KingdeeProductionMaterial kingdeeProductionMaterial ) {
        if (kingdeeProductionMaterial == null) {
            return null;
        }
        ApsMaterial apsMaterial = new ApsMaterial();
        apsMaterial.setFMaterialId(kingdeeProductionMaterial.getFMaterialID());
        apsMaterial.setFSubReqBillNo(kingdeeProductionMaterial.getFMOBillNO());
        apsMaterial.setFMaterialId2(kingdeeProductionMaterial.getFMaterialID2());
        apsMaterial.setFMaterialType(kingdeeProductionMaterial.getFMaterialType());
        apsMaterial.setFMustQty(kingdeeProductionMaterial.getFMustQty());
        apsMaterial.setFPickedQty(kingdeeProductionMaterial.getFPickedQty());
        apsMaterial.setFGoodReturnQty(kingdeeProductionMaterial.getFGoodReturnQty());
        apsMaterial.setFProcessDefectReturnQty(kingdeeProductionMaterial.getFProcessDefectReturnQty());
        apsMaterial.setFormName("生产用料清单列表");
        apsMaterial.setVersion(null);

        return apsMaterial;
    }

}
