package com.benewake.system.excel.transfer;

import com.benewake.system.entity.ApsFinishedProductBasicData;
import com.benewake.system.excel.entity.ExcelFinishedProductTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExcelFinishedProductToPo {
    public ApsFinishedProductBasicData convert(ExcelFinishedProductTemplate object) {
        if (object == null) {
            return null;
        }
        ApsFinishedProductBasicData finishedProductBasicData = new ApsFinishedProductBasicData();
        finishedProductBasicData.setFMaterialCode(object.getMaterialCode());
        finishedProductBasicData.setFMaterialProperty(object.getMaterialProperty());
        finishedProductBasicData.setFMaterialGroup(object.getMaterialGroup());
        finishedProductBasicData.setFProductType(object.getProductType());
        finishedProductBasicData.setFProductFamily(object.getProductFamily());
        finishedProductBasicData.setFPackagingMethod(object.getPackagingMethod());
        finishedProductBasicData.setFMaxAssemblyPersonnel(object.getMaxAssemblyPersonnel());
        finishedProductBasicData.setFMinAssemblyPersonnel(object.getMinAssemblyPersonnel());
        finishedProductBasicData.setFMaxTestingPersonnel(object.getMaxTestingPersonnel());
        finishedProductBasicData.setFMinTestingPersonnel(object.getMinTestingPersonnel());
        finishedProductBasicData.setFMaxPackagingPersonnel(object.getMaxPackagingPersonnel());
        finishedProductBasicData.setFMinPackagingPersonnel(object.getMinPackagingPersonnel());
        finishedProductBasicData.setFMoq(object.getMoq());
        finishedProductBasicData.setFMpq(object.getMpq());
        finishedProductBasicData.setFSafetyStock(object.getSafetyStock());
        return finishedProductBasicData;
    }
}
