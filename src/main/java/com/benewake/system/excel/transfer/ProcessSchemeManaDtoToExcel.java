package com.benewake.system.excel.transfer;

import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement;
import com.benewake.system.excel.entity.ExcelProcessNamePool;
import com.benewake.system.excel.entity.ExcelSchemeManagement;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessSchemeManaDtoToExcel {
    public List<ExcelSchemeManagement> convert(List<ApsProductFamilyProcessSchemeManagement> object) {
        if (CollectionUtils.isEmpty(object)) {
            return null;
        }

        List<ExcelSchemeManagement> excelSchemeManagements = object.stream().map(x -> {
                    ExcelSchemeManagement excelSchemeManagement = new ExcelSchemeManagement();
                    excelSchemeManagement.setProductFamily(x.getProductFamily());
                    excelSchemeManagement.setCurrentProcessScheme(x.getCurProcessSchemeName());
                    excelSchemeManagement.setOptimalProcessPlan(x.getOptimalProcessSchemeName());
                    excelSchemeManagement.setOrderNumber(x.getOrderNumber());
                    excelSchemeManagement.setNumber(x.getNumber());
                    if (x.getProductionLineBalanceRate() != null) {
                        excelSchemeManagement.setProductionLineBalanceRate(x.getProductionLineBalanceRate().multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP) + "%");
                    }
                    if (x.getCompletionTime() != null) {
                        BigDecimal completionTimeInHours = x.getCompletionTime().divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
                        excelSchemeManagement.setCompletionTime(completionTimeInHours);
                    }
                    if (x.getTotalReleaseTime() != null) {
                        double totalReleaseTimeHours = x.getTotalReleaseTime() / 3600;
                        String formattedHours = String.format("%.2f", totalReleaseTimeHours);
                        excelSchemeManagement.setTotalReleaseTime(formattedHours);
                    }
                    excelSchemeManagement.setReleasableStaffCount(x.getReleasableStaffCount());
                    return excelSchemeManagement;
                }
        ).collect(Collectors.toList());
        return excelSchemeManagements;
    }
}
