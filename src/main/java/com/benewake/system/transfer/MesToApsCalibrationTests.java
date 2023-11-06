package com.benewake.system.transfer;

import com.benewake.system.entity.ApsCalibrationTests;
import com.benewake.system.entity.ApsPcbaBurn;
import com.benewake.system.entity.mes.MesCalibrationTests;
import com.benewake.system.entity.mes.MesPcbaBurn;
import org.springframework.stereotype.Component;

@Component
public class MesToApsCalibrationTests {

    public ApsCalibrationTests convert(MesCalibrationTests mesCalibrationTests , Integer version) {
        if (mesCalibrationTests == null || version == null) {
            return null;
        }
        ApsCalibrationTests apsCalibrationTests = new ApsCalibrationTests();
        apsCalibrationTests.setProductionordernumber(mesCalibrationTests.getProductionOrderNumber());
        apsCalibrationTests.setMaterialcode(mesCalibrationTests.getMaterialCode());
        apsCalibrationTests.setMaterialname(mesCalibrationTests.getMaterialName());
        apsCalibrationTests.setBurnincompletionquantity(mesCalibrationTests.getBurnInCompletionQuantity());
        apsCalibrationTests.setBurnqualifiedcount(mesCalibrationTests.getBurnQualifiedCount());
        apsCalibrationTests.setBurnfixturenumber(mesCalibrationTests.getBurnFixtureNumber());
        apsCalibrationTests.setVersion(version);

        return apsCalibrationTests;
    }
}
