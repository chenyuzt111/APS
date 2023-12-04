package com.benewake.system.excel.transfer;

import com.benewake.system.entity.ApsRawMaterialBasicData;
import com.benewake.system.excel.entity.ExcelRawMaterialBasicDataTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExcelRawMaterialToPo {
    public ApsRawMaterialBasicData convert(ExcelRawMaterialBasicDataTemplate object) {
        if (object == null) {
            return null;
        }
        ApsRawMaterialBasicData rawMaterialBasicData = new ApsRawMaterialBasicData();
        rawMaterialBasicData.setFMaterialCode(object.getMaterialCode());
        rawMaterialBasicData.setFMaterialProperty(object.getMaterialProperty());
        rawMaterialBasicData.setFMaterialGroup(object.getMaterialGroup());
        rawMaterialBasicData.setFProcurementLeadTime(object.getProcurementLeadTime());
        rawMaterialBasicData.setFMoq(object.getMoq());
        rawMaterialBasicData.setFMpq(object.getMpq());
        rawMaterialBasicData.setFSafetyStock(object.getSafetyStock());
        return rawMaterialBasicData;
    }
}
