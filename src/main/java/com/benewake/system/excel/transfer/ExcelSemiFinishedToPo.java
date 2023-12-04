package com.benewake.system.excel.transfer;

import com.benewake.system.entity.ApsRawMaterialBasicData;
import com.benewake.system.entity.ApsSemiFinishedBasicData;
import com.benewake.system.excel.entity.ExcelRawMaterialBasicDataTemplate;
import com.benewake.system.excel.entity.ExcelSemiFinishedTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExcelSemiFinishedToPo {
    public ApsSemiFinishedBasicData convert(ExcelSemiFinishedTemplate object) {
        if (object == null) {
            return null;
        }
        ApsSemiFinishedBasicData semiFinishedBasicData = new ApsSemiFinishedBasicData();
        semiFinishedBasicData.setFMaterialCode(object.getMaterialCode());
        semiFinishedBasicData.setFMaterialProperty(object.getMaterialProperty());
        semiFinishedBasicData.setFMaterialGroup(object.getMaterialGroup());
        semiFinishedBasicData.setFProductType(object.getProductType());
        semiFinishedBasicData.setFProcurementLeadTime(object.getProcurementLeadTime());
        semiFinishedBasicData.setFMoq(object.getMoq());
        semiFinishedBasicData.setFMpq(object.getMpq());
        semiFinishedBasicData.setFSafetyStock(object.getSafetyStock());
        return semiFinishedBasicData;
    }
}
