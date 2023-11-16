package com.benewake.system.excel.transfer;

import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.vo.ApsProcessSchemeVo;
import com.benewake.system.excel.entity.ExcelProcessNamePool;
import com.benewake.system.excel.entity.ExcelProcessScheme;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessSchemeVoToExcelList {

    public List<ExcelProcessScheme> convert(List<ApsProcessSchemeVo> object) {
        if (CollectionUtils.isEmpty(object)) {
            return null;
        }

        List<ExcelProcessScheme> excelProcessSchemes = object.stream().map(x -> {
            ExcelProcessScheme excelProcessScheme = new ExcelProcessScheme();
            excelProcessScheme.setCurrentProcessScheme(x.getCurrentProcessScheme());
            excelProcessScheme.setBelongingProcess(x.getBelongingProcess());
            excelProcessScheme.setProductFamily(x.getProductFamily());
            excelProcessScheme.setProcessNumber(x.getProcessNumber());
            excelProcessScheme.setProcessName(x.getProcessName());
            excelProcessScheme.setPackagingMethod(x.getPackagingMethod());
            excelProcessScheme.setStandardTime(x.getStandardTime());
            excelProcessScheme.setSwitchTime(x.getSwitchTime());
            excelProcessScheme.setMaxPersonnel(x.getMaxPersonnel());
            excelProcessScheme.setMinPersonnel(x.getMinPersonnel());
            excelProcessScheme.setEmployeeName(x.getEmployeeName());
            excelProcessScheme.setNumber(x.getNumber());
            excelProcessScheme.setState(x.getState());
            return excelProcessScheme;
        }).collect(Collectors.toList());
        return excelProcessSchemes;
    }
}
