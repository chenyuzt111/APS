package com.benewake.system.transfer;

import com.benewake.system.entity.ApsMaterial;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedMaterial;
import org.springframework.stereotype.Component;

@Component
public class KingdeeOutsourcedToApsMaterial {

    public ApsMaterial convert(KingdeeOutsourcedMaterial kingdeeOutsourcedMaterial) {
        if (kingdeeOutsourcedMaterial == null) {
            return null;
        }
        ApsMaterial apsMaterial = new ApsMaterial();
        apsMaterial.setFMaterialId(kingdeeOutsourcedMaterial.getFMaterialID());
//        apsOutsourcedMaterial.setFMaterialName(kingdeeOutsourcedMaterial.getFMaterialName());
        apsMaterial.setFSubReqBillNo(kingdeeOutsourcedMaterial.getFSubReqBillNO());
        apsMaterial.setFMaterialId2(kingdeeOutsourcedMaterial.getFMaterialID2());
//        apsOutsourcedMaterial.setFMaterialName2(kingdeeOutsourcedMaterial.getFMaterialName2());
        apsMaterial.setFMaterialType(kingdeeOutsourcedMaterial.getFMaterialType());
        apsMaterial.setFMustQty(kingdeeOutsourcedMaterial.getFMustQty());
        apsMaterial.setFPickedQty(kingdeeOutsourcedMaterial.getFPickedQty());
        apsMaterial.setFGoodReturnQty(kingdeeOutsourcedMaterial.getFGoodReturnQty());
        apsMaterial.setFProcessDefectReturnQty(kingdeeOutsourcedMaterial.getFProcessDefectReturnQty());
        apsMaterial.setFormName("委外用料清单列表");
        apsMaterial.setVersion(null);

        return apsMaterial;
    }

}
