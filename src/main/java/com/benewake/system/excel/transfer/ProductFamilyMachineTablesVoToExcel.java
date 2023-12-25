package com.benewake.system.excel.transfer;

import com.benewake.system.entity.vo.ApsProductFamilyMachineTableVo;
import com.benewake.system.excel.entity.ExcelProductFamilyMachineTable;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductFamilyMachineTablesVoToExcel {

    public List<ExcelProductFamilyMachineTable> convert(List<ApsProductFamilyMachineTableVo> object) {
        if (CollectionUtils.isEmpty(object)) {
            return null;
        }
        ArrayList<ExcelProductFamilyMachineTable> excelProductFamilyMachineTables = new ArrayList<>(object.size());
        for (ApsProductFamilyMachineTableVo apsProductFamilyMachineTableVo : object) {
            ExcelProductFamilyMachineTable excelProductFamilyMachineTable = new ExcelProductFamilyMachineTable();
//            excelProductFamilyMachineTable.setNumber(apsProductFamilyMachineTableVo.getNumber());
//            excelProductFamilyMachineTable.setFMachineId(apsProductFamilyMachineTableVo.getFMachineId());
            excelProductFamilyMachineTable.setFMachineName(apsProductFamilyMachineTableVo.getFMachineName());
            excelProductFamilyMachineTable.setFProductFamily(apsProductFamilyMachineTableVo.getFProductFamily());
//            excelProductFamilyMachineTable.setFProcessId(apsProductFamilyMachineTableVo.getFProcessId());
            excelProductFamilyMachineTable.setProcessName(apsProductFamilyMachineTableVo.getProcessName());
            excelProductFamilyMachineTable.setFMachineConfiguration(apsProductFamilyMachineTableVo.getFMachineConfiguration());
            excelProductFamilyMachineTable.setFWorkshop(apsProductFamilyMachineTableVo.getFWorkshop());
            excelProductFamilyMachineTable.setAvailable(apsProductFamilyMachineTableVo.getAvailable());
            List<String> unavailableDates = apsProductFamilyMachineTableVo.getUnavailableDates();
            if (CollectionUtils.isNotEmpty(unavailableDates)) {
                String unavailDate = String.join(",", unavailableDates);
                excelProductFamilyMachineTable.setUnavailableDates(unavailDate);
            }
            excelProductFamilyMachineTables.add(excelProductFamilyMachineTable);
        }

        return excelProductFamilyMachineTables;
    }
}
