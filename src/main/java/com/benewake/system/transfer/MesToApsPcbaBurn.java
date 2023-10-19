package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPcbaBurn;
import com.benewake.system.entity.mes.MesPcbaBurn;
import org.springframework.stereotype.Component;

@Component
public class MesToApsPcbaBurn {
    public ApsPcbaBurn convert(MesPcbaBurn mesPcbaBurn , Integer version) {
        if (mesPcbaBurn == null || version == null) {
            return null;
        }

        ApsPcbaBurn apsPcbaBurn = new ApsPcbaBurn();
        apsPcbaBurn.setProductionOrderNumber(mesPcbaBurn.getProductionOrderNumber());
        apsPcbaBurn.setMaterialCode(mesPcbaBurn.getMaterialCode());
        apsPcbaBurn.setMaterialName(mesPcbaBurn.getMaterialName());
        apsPcbaBurn.setBurnInCompletionQuantity(mesPcbaBurn.getBurnInCompletionQuantity());
        apsPcbaBurn.setBurnQualifiedCount(mesPcbaBurn.getBurnQualifiedCount());
        apsPcbaBurn.setBurnFixtureNumber(mesPcbaBurn.getBurnFixtureNumber());
        apsPcbaBurn.setVersion(version);
        return apsPcbaBurn;

    }
}
