package com.benewake.system.transfer;

import com.benewake.system.entity.ApsOutsourcedMaterial;
import com.benewake.system.entity.ApsProductionMaterial;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedMaterial;
import com.benewake.system.entity.kingdee.KingdeeProductionMaterial;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsProductionMaterial {

    public ApsProductionMaterial convert(KingdeeProductionMaterial kingdeeProductionMaterial , Integer version) {
        if (kingdeeProductionMaterial == null || version == null) {
            return null;
        }
        ApsProductionMaterial apsProductionMaterial = new ApsProductionMaterial();
        apsProductionMaterial.setFMaterialId(kingdeeProductionMaterial.getFMaterialID());
//        apsProductionMaterial.setFMaterialName(kingdeeProductionMaterial.getFMaterialName());
        apsProductionMaterial.setFMoBillNo(kingdeeProductionMaterial.getFMOBillNO());
        apsProductionMaterial.setFMaterialId2(kingdeeProductionMaterial.getFMaterialID2());
//        apsProductionMaterial.setFMaterialName2(kingdeeProductionMaterial.getFMaterialName1());
        apsProductionMaterial.setFMaterialType(kingdeeProductionMaterial.getFMaterialType());
        apsProductionMaterial.setFMustQty(kingdeeProductionMaterial.getFMustQty());
        apsProductionMaterial.setFPickedQty(kingdeeProductionMaterial.getFPickedQty());
        apsProductionMaterial.setFGoodReturnQty(kingdeeProductionMaterial.getFGoodReturnQty());
        apsProductionMaterial.setFProcessDefectReturnQty(kingdeeProductionMaterial.getFProcessDefectReturnQty());
        apsProductionMaterial.setVersion(version);

        return apsProductionMaterial;
    }

}
