//package com.benewake.system.excel.transfer;
//
//import com.benewake.system.entity.ApsProcessCapacity;
//import com.benewake.system.excel.entity.ExcelProcessCapacity;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.Map;
//
//@Component
//public class ProcessCapacityExcelToPo {
//    public ApsProcessCapacity convert(ExcelProcessCapacity object, Map<String, Integer> processNameMap) {
//        if (object == null) {
//            return null;
//        }
//        ApsProcessCapacity apsProcessCapacity = new ApsProcessCapacity();
//        apsProcessCapacity.setBelongingProcess(object.getBelongingProcess());
//        apsProcessCapacity.setProcessId();
//        apsProcessCapacity.setProcessNumber(object.getProcessNumber());
//        apsProcessCapacity.setProductFamily(object.getProductFamily());
//        apsProcessCapacity.setPackagingMethod(object.getPackagingMethod());
//        apsProcessCapacity.setSwitchTime(object.getSwitchTime());
//        if (StringUtils.isNotEmpty(object.getStandardTime())) {
//            apsProcessCapacity.setStandardTime(new BigDecimal(object.getStandardTime()));
//        }
//        apsProcessCapacity.setMaxPersonnel(object.getMaxPersonnel());
//        apsProcessCapacity.setMinPersonnel(object.getMinPersonnel());
//
//        return apsProcessCapacity;
//    }
//}
