package com.benewake.system.transfer;

import com.benewake.system.entity.ApsCalibrationTests;
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
        apsTfminiSCalibrationTests.setProductionordernumber(mesCalibrationTests.getProductionOrderNumber());
        apsTfminiSCalibrationTests.setMaterialcode(mesCalibrationTests.getMaterialCode());
        apsTfminiSCalibrationTests.setMaterialname(mesCalibrationTests.getMaterialName());
        apsTfminiSCalibrationTests.setBurnincompletionquantity(mesCalibrationTests.getBurnInCompletionQuantity());
        apsTfminiSCalibrationTests.setBurnqualifiedcount(mesCalibrationTests.getBurnQualifiedCount());
        apsTfminiSCalibrationTests.setBurnfixturenumber(mesCalibrationTests.getBurnFixtureNumber());
        apsTfminiSCalibrationTests.setTotalNumber(mesCalibrationTests.getTotalNumber());
        apsTfminiSCalibrationTests.setUnburnQualifiedCount(mesCalibrationTests.getUnBurnQualifiedCount());
        apsTfminiSCalibrationTests.setVersion(version);

        return apsTfminiSCalibrationTests;
    }
}
