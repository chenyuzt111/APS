package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPcbaBurn;
import com.benewake.system.entity.ApsTfminiSPcbaBurn;
import com.benewake.system.entity.mes.MesPcbaBurn;
import org.springframework.stereotype.Component;

@Component
public class MesToApsTfminiSPcbaBurn {
    public ApsTfminiSPcbaBurn convert(MesPcbaBurn mesPcbaBurn, Integer version) {
        if (mesPcbaBurn == null || version == null) {
            return null;
        }
        ApsTfminiSPcbaBurn apsTfminiSPcbaBurn = new ApsTfminiSPcbaBurn();
        apsTfminiSPcbaBurn.setProductionOrderNumber(mesPcbaBurn.getProductionOrderNumber());
        apsTfminiSPcbaBurn.setMaterialCode(mesPcbaBurn.getMaterialCode());
        apsTfminiSPcbaBurn.setMaterialName(mesPcbaBurn.getMaterialName());
        apsTfminiSPcbaBurn.setBurnInCompletionQuantity(mesPcbaBurn.getBurnInCompletionQuantity());
        apsTfminiSPcbaBurn.setBurnQualifiedCount(mesPcbaBurn.getBurnQualifiedCount());
        apsTfminiSPcbaBurn.setBurnFixtureNumber(mesPcbaBurn.getBurnFixtureNumber());
        apsTfminiSPcbaBurn.setTotalNumber(mesPcbaBurn.getTotalNumber());
        apsTfminiSPcbaBurn.setUnburnQualifiedCount(mesPcbaBurn.getUnBurnQualifiedCount());
        apsTfminiSPcbaBurn.setVersion(version);
        return apsTfminiSPcbaBurn;
    }
}
