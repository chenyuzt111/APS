package com.benewake.system.transfer;

import com.benewake.system.entity.ApsMaterialBom;
import com.benewake.system.entity.ApsOutsourcedMaterial;
import com.benewake.system.entity.kingdee.KingdeeMaterialBom;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedMaterial;
import org.springframework.stereotype.Component;

@Component
public class KingdeeToApsMaterialBom {

    public ApsMaterialBom convert(KingdeeMaterialBom kingdeeMaterialBom , Integer version) {
        if (kingdeeMaterialBom == null || version == null) {
            return null;
        }

        ApsMaterialBom apsMaterialBom = new ApsMaterialBom();
        apsMaterialBom.setFMaterialId(kingdeeMaterialBom.getFMaterialID());
        apsMaterialBom.setFMaterialName(kingdeeMaterialBom.getFITEMNAME());
        apsMaterialBom.setFDocumentStatus(kingdeeMaterialBom.getFDocumentStatus());
        apsMaterialBom.setFMaterialIdChild(kingdeeMaterialBom.getFMaterialIDChild());
        apsMaterialBom.setFMaterialNameChild(kingdeeMaterialBom.getFCHILDITEMNAME());
        apsMaterialBom.setFNumerator(kingdeeMaterialBom.getFNumerator());
        apsMaterialBom.setFDenominator(kingdeeMaterialBom.getFDenominator());
        apsMaterialBom.setFFixScrapQtyLot(kingdeeMaterialBom.getFFixScrapQtyLot());
        apsMaterialBom.setFScrapRate(kingdeeMaterialBom.getFScrapRate());
        apsMaterialBom.setFMaterialType(kingdeeMaterialBom.getFMaterialType());
        apsMaterialBom.setFReplaceType(kingdeeMaterialBom.getFReplaceType());
        apsMaterialBom.setFReplaceGroupBop(kingdeeMaterialBom.getFReplaceGroup());
        apsMaterialBom.setBomVersion(kingdeeMaterialBom.getFNumber());
        apsMaterialBom.setVersion(version);

        return apsMaterialBom;
    }

}
