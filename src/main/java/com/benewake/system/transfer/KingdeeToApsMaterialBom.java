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
        apsMaterialBom.setFNumber(kingdeeMaterialBom.getFNumber());
        apsMaterialBom.setFUseOrgId(kingdeeMaterialBom.getFUseOrgId());
        apsMaterialBom.setFMaterialId(kingdeeMaterialBom.getFMaterialID());
        apsMaterialBom.setFDocumentStatus(kingdeeMaterialBom.getFDocumentStatus());
        apsMaterialBom.setFMaterialIdChild(kingdeeMaterialBom.getFMaterialIDChild());
        apsMaterialBom.setFNumerator(kingdeeMaterialBom.getFNumerator());
        apsMaterialBom.setFDenominator(kingdeeMaterialBom.getFDenominator());
        apsMaterialBom.setFFixScrapQtyLot(kingdeeMaterialBom.getFFixScrapQtyLot());
        apsMaterialBom.setFScrapRate(kingdeeMaterialBom.getFScrapRate());
        apsMaterialBom.setFForbidStatus(kingdeeMaterialBom.getFForBidStatus());
        apsMaterialBom.setFExpireDate(kingdeeMaterialBom.getFExpireDate());
        apsMaterialBom.setFMaterialType(kingdeeMaterialBom.getFMaterialType());
        apsMaterialBom.setFReplaceType(kingdeeMaterialBom.getFReplaceType());
        apsMaterialBom.setVersion(version);

        return apsMaterialBom;
    }

}
