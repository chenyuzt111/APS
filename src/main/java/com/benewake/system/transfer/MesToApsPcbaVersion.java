package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPcbaBurn;
import com.benewake.system.entity.ApsPcbaVersion;
import com.benewake.system.entity.mes.MesPcbaBurn;
import com.benewake.system.entity.mes.MesPcbaVersion;
import org.springframework.stereotype.Component;

@Component
public class MesToApsPcbaVersion {
    public ApsPcbaVersion convert(MesPcbaVersion mesPcbaVersion, Integer version) {
        if (mesPcbaVersion == null || version == null) {
            return null;
        }
        ApsPcbaVersion apsPcbaVersion = new ApsPcbaVersion();
        apsPcbaVersion.setProductionOrderNumber(mesPcbaVersion.getProductionOrderNumber());
        apsPcbaVersion.setMaterialCode(mesPcbaVersion.getMaterialCode());
        apsPcbaVersion.setMaterialName(mesPcbaVersion.getMaterialName());
        apsPcbaVersion.setBurnInCompletionQuantity(mesPcbaVersion.getBurnInCompletionQuantity());
        apsPcbaVersion.setBurnQualifiedCount(mesPcbaVersion.getBurnQualifiedCount());
        apsPcbaVersion.setBurnFixtureNumber(mesPcbaVersion.getBurnFixtureNumber());
        apsPcbaVersion.setUnburnQualifiedCount(mesPcbaVersion.getUnBurnQualifiedCount());
        apsPcbaVersion.setTotalNumber(mesPcbaVersion.getTotalNumber());
        apsPcbaVersion.setVersion(version);
        return apsPcbaVersion;
    }
}
