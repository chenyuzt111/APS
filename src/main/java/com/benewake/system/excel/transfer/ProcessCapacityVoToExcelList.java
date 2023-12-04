package com.benewake.system.excel.transfer;

import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import com.benewake.system.excel.entity.ExcelProcessCapacity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessCapacityVoToExcelList {
    public List<ExcelProcessCapacity> convert(List<ApsProcessCapacityVo> object) {
        if (CollectionUtils.isEmpty(object)) {
            return null;
        }
        List<ExcelProcessCapacity> excelProcessCapacities = object.stream().map(x -> {
            ExcelProcessCapacity excelProcessCapacity = new ExcelProcessCapacity();
            excelProcessCapacity.setBelongingProcess(x.getBelongingProcess());
            excelProcessCapacity.setProcessName(x.getProcessName());
            excelProcessCapacity.setProcessNumber(x.getProcessNumber());
            excelProcessCapacity.setProductFamily(x.getProductFamily());
            excelProcessCapacity.setPackagingMethod(x.getPackagingMethod());
            excelProcessCapacity.setStandardTime(x.getStandardTime());
            excelProcessCapacity.setSwitchTime(x.getSwitchTime());
            excelProcessCapacity.setMaxPersonnel(x.getMaxPersonnel());
            excelProcessCapacity.setMinPersonnel(x.getMinPersonnel());
            return excelProcessCapacity;
        }).collect(Collectors.toList());
        return excelProcessCapacities;
    }
}
