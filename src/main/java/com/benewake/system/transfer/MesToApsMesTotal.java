package com.benewake.system.transfer;

import com.benewake.system.entity.ApsMesTotal;
import com.benewake.system.entity.ApsPcbaBurn;
import com.benewake.system.entity.mes.MesPcbaBurn;
import com.benewake.system.entity.mes.MesTotal;
import org.springframework.stereotype.Component;

@Component
public class MesToApsMesTotal {
    public ApsMesTotal convert(MesTotal mesTotal , Integer version) {
        if (mesTotal == null || version == null) {
            return null;
        }

        ApsMesTotal apsMesTotal = new ApsMesTotal();
        apsMesTotal.setProductionOrderNumber(mesTotal.getProductionOrderNumber());
        apsMesTotal.setMaterialCode(mesTotal.getMaterialCode());
        apsMesTotal.setBurnInCompletionQuantity(mesTotal.getBurnInCompletionQuantity());
        apsMesTotal.setBurnQualifiedCount(mesTotal.getBurnQualifiedCount());
        apsMesTotal.setBurnFixtureNumber(mesTotal.getBurnFixtureNumber());
        apsMesTotal.setUnBurnQualifiedCount(mesTotal.getUnBurnQualifiedCount());
        apsMesTotal.setTotalNumber(mesTotal.getTotalNumber());
        apsMesTotal.setVersion(version);
        return apsMesTotal;

    }
}
