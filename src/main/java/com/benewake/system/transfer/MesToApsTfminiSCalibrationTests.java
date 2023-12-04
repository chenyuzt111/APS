package com.benewake.system.transfer;

import com.benewake.system.entity.ApsTfminiSCalibrationTests;
import com.benewake.system.entity.mes.MesCalibrationTests;
import org.springframework.stereotype.Component;

@Component
public class MesToApsTfminiSCalibrationTests {
    public ApsTfminiSCalibrationTests convert(MesCalibrationTests mesCalibrationTests , Integer version) {
        if (mesCalibrationTests == null || version == null) {
            return null;
        }
        ApsTfminiSCalibrationTests apsTfminiSCalibrationTests = new ApsTfminiSCalibrationTests();
        apsTfminiSCalibrationTests.setProductionOrderNumber(mesCalibrationTests.getProductionOrderNumber());
        apsTfminiSCalibrationTests.setMaterialCode(mesCalibrationTests.getMaterialCode());
//        apsTfminiSCalibrationTests.setMaterialname(mesCalibrationTests.getMaterialName());
        apsTfminiSCalibrationTests.setBurnInCompletionQuantity(mesCalibrationTests.getBurnInCompletionQuantity());
        apsTfminiSCalibrationTests.setBurnQualifiedCount(mesCalibrationTests.getBurnQualifiedCount());
        apsTfminiSCalibrationTests.setBurnFixtureNumber(mesCalibrationTests.getBurnFixtureNumber());
        apsTfminiSCalibrationTests.setTotalNumber(mesCalibrationTests.getTotalNumber());
        apsTfminiSCalibrationTests.setUnBurnQualifiedCount(mesCalibrationTests.getUnBurnQualifiedCount());
        apsTfminiSCalibrationTests.setVersion(version);

        return apsTfminiSCalibrationTests;
    }
}
