package com.benewake.system.transfer;

import com.benewake.system.entity.ApsCalibrationTests;
import com.benewake.system.entity.mes.MesCalibrationTests;
import org.springframework.stereotype.Component;

@Component
public class MesToApsCalibrationTests {

    public ApsCalibrationTests convert(MesCalibrationTests mesCalibrationTests , Integer version) {
        if (mesCalibrationTests == null || version == null) {
            return null;
        }
        ApsCalibrationTests apsCalibrationTests = new ApsCalibrationTests();
        apsCalibrationTests.setProductionOrderNumber(mesCalibrationTests.getProductionOrderNumber());
        apsCalibrationTests.setMaterialCode(mesCalibrationTests.getMaterialCode());
//        apsCalibrationTests.setMaterialname(mesCalibrationTests.getMaterialName());
        apsCalibrationTests.setBurnInCompletionQuantity(mesCalibrationTests.getBurnInCompletionQuantity());
        apsCalibrationTests.setBurnQualifiedCount(mesCalibrationTests.getBurnQualifiedCount());
        apsCalibrationTests.setUnBurnQualifiedCount(mesCalibrationTests.getUnBurnQualifiedCount());
        apsCalibrationTests.setBurnFixtureNumber(mesCalibrationTests.getBurnFixtureNumber());
        apsCalibrationTests.setTotalNumber(mesCalibrationTests.getTotalNumber());
        apsCalibrationTests.setVersion(version);

        return apsCalibrationTests;
    }
}
