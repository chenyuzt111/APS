package com.benewake.system.transfer;

import com.benewake.system.entity.ApsPackagingTest;
import com.benewake.system.entity.ApsPcbaBurn;
import com.benewake.system.entity.mes.MesPackagingTest;
import com.benewake.system.entity.mes.MesPcbaBurn;
import org.springframework.stereotype.Component;

@Component
public class MesToApsPackagingTest {

    public ApsPackagingTest convert(MesPackagingTest mesPackagingTest , Integer version) {
        if (mesPackagingTest == null || version == null) {
            return null;
        }
        ApsPackagingTest apsPackagingTest = new ApsPackagingTest();
        apsPackagingTest.setProductionOrderNumber(mesPackagingTest.getProductionOrderNumber());
        apsPackagingTest.setMaterialCode(mesPackagingTest.getMaterialCode());
        apsPackagingTest.setMaterialName(mesPackagingTest.getMaterialName());
        apsPackagingTest.setBurnInCompletionQuantity(mesPackagingTest.getBurnInCompletionQuantity());
        apsPackagingTest.setBurnQualifiedCount(mesPackagingTest.getBurnQualifiedCount());
        apsPackagingTest.setTotalNumber(mesPackagingTest.getTotalNumber());
        apsPackagingTest.setUnburnQualifiedCount(mesPackagingTest.getUnBurnQualifiedCount());
        apsPackagingTest.setVersion(version);

        return apsPackagingTest;

    }
}
