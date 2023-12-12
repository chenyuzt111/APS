package com.benewake.system.transfer;

import com.benewake.system.entity.ApsTfminiSFixed;
import com.benewake.system.entity.mes.MesTfminiSFixed;
import org.springframework.stereotype.Component;


@Component
public class MesToApsTfminiSFixed {


    public ApsTfminiSFixed convert(MesTfminiSFixed mesTfminiSFixed, Integer version){
        if (mesTfminiSFixed == null || version == null) {
            return null;
        }
        ApsTfminiSFixed apsTfminiSFixed = new ApsTfminiSFixed();

        apsTfminiSFixed.setProductionOrderNumber(mesTfminiSFixed.getProductionOrderNumber());
        apsTfminiSFixed.setMaterialCode(mesTfminiSFixed.getMaterialCode());
        apsTfminiSFixed.setBurnInCompletionQuantity(mesTfminiSFixed.getBurnInCompletionQuantity());
        apsTfminiSFixed.setBurnQualifiedCount(mesTfminiSFixed.getBurnQualifiedCount());
        apsTfminiSFixed.setBurnFixtureNumber(mesTfminiSFixed.getBurnFixtureNumber());
        apsTfminiSFixed.setTotalNumber(mesTfminiSFixed.getTotalNumber());
        apsTfminiSFixed.setUnBurnQualifiedCount(mesTfminiSFixed.getUnBurnQualifiedCount());
        apsTfminiSFixed.setVersion(version);
        return apsTfminiSFixed;

    }
}
