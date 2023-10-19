package com.benewake.system.transfer;

import com.benewake.system.entity.ApsOutsourcedMaterial;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedMaterial;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsOutsourcedMaterial {

    public ApsOutsourcedMaterial convert(KingdeeOutsourcedMaterial kingdeeOutsourcedMaterial , Integer version) {
        if (kingdeeOutsourcedMaterial == null || version == null) {
            return null;
        }
        ApsOutsourcedMaterial apsOutsourcedMaterial = new ApsOutsourcedMaterial();
        apsOutsourcedMaterial.setFMaterialId(kingdeeOutsourcedMaterial.getFMaterialID());
        apsOutsourcedMaterial.setFMaterialName(kingdeeOutsourcedMaterial.getFMaterialName());
        apsOutsourcedMaterial.setFSubReqBillNo(kingdeeOutsourcedMaterial.getFSubReqBillNO());
        apsOutsourcedMaterial.setFMaterialId2(kingdeeOutsourcedMaterial.getFMaterialID2());
        apsOutsourcedMaterial.setFMaterialName2(kingdeeOutsourcedMaterial.getFMaterialName2());
        apsOutsourcedMaterial.setFMaterialType(kingdeeOutsourcedMaterial.getFMaterialType());
        apsOutsourcedMaterial.setFMustQty(kingdeeOutsourcedMaterial.getFMustQty());
        apsOutsourcedMaterial.setFPickedQty(kingdeeOutsourcedMaterial.getFPickedQty());
        apsOutsourcedMaterial.setFGoodReturnQty(kingdeeOutsourcedMaterial.getFGoodReturnQty());
        apsOutsourcedMaterial.setFProcessDefectReturnQty(kingdeeOutsourcedMaterial.getFProcessDefectReturnQty());
        apsOutsourcedMaterial.setVersion(version);

        return apsOutsourcedMaterial;
    }

}
