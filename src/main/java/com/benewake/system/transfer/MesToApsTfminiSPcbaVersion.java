package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPcbaVersion;
import com.benewake.system.entity.ApsTfminiSPcbaVersion;
import com.benewake.system.entity.mes.MesPcbaVersion;
import org.springframework.stereotype.Component;

@Component
public class MesToApsTfminiSPcbaVersion {

    public ApsTfminiSPcbaVersion convert(MesPcbaVersion mesPcbaVersion, Integer version) {
        if (mesPcbaVersion == null || version == null) {
            return null;
        }

        ApsTfminiSPcbaVersion apsTfminiSPcbaVersion = new ApsTfminiSPcbaVersion();
        apsTfminiSPcbaVersion.setProductionOrderNumber(mesPcbaVersion.getProductionOrderNumber());
        apsTfminiSPcbaVersion.setMaterialCode(mesPcbaVersion.getMaterialCode());
//        apsTfminiSPcbaVersion.setMaterialName(mesPcbaVersion.getMaterialName());
        apsTfminiSPcbaVersion.setBurnInCompletionQuantity(mesPcbaVersion.getBurnInCompletionQuantity());
        apsTfminiSPcbaVersion.setBurnQualifiedCount(mesPcbaVersion.getBurnQualifiedCount());
        apsTfminiSPcbaVersion.setBurnFixtureNumber(mesPcbaVersion.getBurnFixtureNumber());
        apsTfminiSPcbaVersion.setTotalNumber(mesPcbaVersion.getTotalNumber());
        apsTfminiSPcbaVersion.setUnburnQualifiedCount(mesPcbaVersion.getUnBurnQualifiedCount());
        apsTfminiSPcbaVersion.setVersion(version);

        return apsTfminiSPcbaVersion;
    }

}
