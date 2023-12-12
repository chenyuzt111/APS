package com.benewake.system.excel.transfer;

import com.benewake.system.entity.dto.ApsProcessCapacityDto;
import com.benewake.system.excel.entity.ExcelProcessCapacity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessCapacityDtoToExcelList {

    public List<ExcelProcessCapacity> convert(List<ApsProcessCapacityDto> object) {
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
            excelProcessCapacity.setSwitchTime(x.getSwitchTime());
            if (x.getStandardTime() != null) {
                excelProcessCapacity.setStandardTime(String.valueOf(x.getStandardTime()));
            }
            excelProcessCapacity.setMaxPersonnel(x.getMaxPersonnel());
            excelProcessCapacity.setMinPersonnel(x.getMinPersonnel());
            return excelProcessCapacity;
        }).collect(Collectors.toList());
        return excelProcessCapacities;
    }
}
