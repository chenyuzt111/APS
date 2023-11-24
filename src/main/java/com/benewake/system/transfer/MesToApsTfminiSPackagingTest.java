package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPackagingTest;
import com.benewake.system.entity.ApsTfminiSPackagingTest;
import com.benewake.system.entity.mes.MesPackagingTest;
import org.springframework.stereotype.Component;

@Component
public class MesToApsTfminiSPackagingTest {

    public ApsTfminiSPackagingTest convert(MesPackagingTest mesPackagingTest , Integer version) {
        if (mesPackagingTest == null || version == null) {
            return null;
        }
        ApsTfminiSPackagingTest apsTfminiSPackagingTest = new ApsTfminiSPackagingTest();
        apsTfminiSPackagingTest.setProductionOrderNumber(mesPackagingTest.getProductionOrderNumber());
        apsTfminiSPackagingTest.setMaterialCode(mesPackagingTest.getMaterialCode());
//        apsTfminiSPackagingTest.setMaterialName(mesPackagingTest.getMaterialName());
        apsTfminiSPackagingTest.setBurnInCompletionQuantity(mesPackagingTest.getBurnInCompletionQuantity());
        apsTfminiSPackagingTest.setBurnQualifiedCount(mesPackagingTest.getBurnQualifiedCount());
        apsTfminiSPackagingTest.setTotalNumber(mesPackagingTest.getTotalNumber());
        apsTfminiSPackagingTest.setUnburnQualifiedCount(mesPackagingTest.getUnBurnQualifiedCount());
        apsTfminiSPackagingTest.setVersion(version);

        return apsTfminiSPackagingTest;

    }
}
