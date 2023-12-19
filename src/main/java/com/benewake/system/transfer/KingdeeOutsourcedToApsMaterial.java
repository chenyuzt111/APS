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
        apsMaterial.setMaterialId(kingdeeOutsourcedMaterial.getFMaterialID());
//        apsOutsourcedMaterial.setFMaterialName(kingdeeOutsourcedMaterial.getFMaterialName());
        apsMaterial.setSubReqBillNo(kingdeeOutsourcedMaterial.getFSubReqBillNO());
        apsMaterial.setMaterialId2(kingdeeOutsourcedMaterial.getFMaterialID2());
//        apsOutsourcedMaterial.setFMaterialName2(kingdeeOutsourcedMaterial.getFMaterialName2());
        apsMaterial.setMaterialType(kingdeeOutsourcedMaterial.getFMaterialType());
        apsMaterial.setMustQty(kingdeeOutsourcedMaterial.getFMustQty());
        apsMaterial.setPickedQty(kingdeeOutsourcedMaterial.getFPickedQty());
        apsMaterial.setGoodReturnQty(kingdeeOutsourcedMaterial.getFGoodReturnQty());
        apsMaterial.setProcessDefectReturnQty(kingdeeOutsourcedMaterial.getFProcessDefectReturnQty());
        apsMaterial.setFormName("委外用料清单列表");
        apsMaterial.setVersion(null);

        return apsMaterial;
    }

}
