package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelMachineTableTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.service.ApsProductFamilyMachineTableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class MachineTableListener extends AnalysisEventListener<ExcelMachineTableTemplate> {

    private final List<ApsProductFamilyMachineTable> productFamilyMachineTables = new ArrayList<>();

    private final ApsProductFamilyMachineTableService familyMachineTableService;

    private final Map<String, Integer> processNameToIdMap;

    private final Integer type;

    private final StringBuilder errorString = new StringBuilder();

    public MachineTableListener(ApsProductFamilyMachineTableService familyMachineTableService, ApsProcessNamePoolService processNamePoolService, Integer type) {
        this.familyMachineTableService = familyMachineTableService;
        List<ApsProcessNamePool> processNamePools = processNamePoolService.list();
        processNameToIdMap = processNamePools
                .stream()
                .collect(Collectors.toMap(ApsProcessNamePool::getProcessName, ApsProcessNamePool::getId));
        this.type = type;
    }

    @Override
    public void invoke(ExcelMachineTableTemplate data, AnalysisContext context) {
        ApsProductFamilyMachineTable familyMachineTable = new ApsProductFamilyMachineTable();
        familyMachineTable.setFMachineName(data.getFMachineName());
        familyMachineTable.setFProductFamily(data.getFProductFamily());
        String processName = data.getProcessName();
        if (StringUtils.isNotEmpty(processName)) {
            Integer processId = processNameToIdMap.get(processName);
            if (processId != null) {
                familyMachineTable.setFProcessId(processId);
            } else {
                errorString.append(processName).append("、");
                return;
            }
        }
        familyMachineTable.setFMachineConfiguration(data.getFMachineConfiguration());
        familyMachineTable.setFWorkshop(data.getFWorkshop());
//        String unavailableDates = data.getUnavailableDates();
//        if (StringUtils.isNotEmpty(unavailableDates)) {
//            if (isValidFormat(unavailableDates)) {
//                familyMachineTable.setUnavailableDates(unavailableDates);
//            } else {
//                throw new BeneWakeException("不可以用时间段 时间格式不正确 应以 to 分割不同时间 逗号分割不同时间段");
//            }
//        }
        productFamilyMachineTables.add(familyMachineTable);
    }


    private static boolean isValidFormat(String input) {
        String pattern = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} to \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        Pattern regex = Pattern.compile(pattern);

        String[] split = input.split(",");
        for (String s : split) {
            Matcher matcher = regex.matcher(s);
            boolean matches = matcher.matches();
            if (!matches) return false;
        }
        return true;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (StringUtils.isNotEmpty(errorString)) {
            throw new BeneWakeException(errorString + "工序在工序命名池中不存在");
        }

        if(type == ExcelOperationEnum.OVERRIDE.getCode()) {
            familyMachineTableService.remove(null);
        }
        familyMachineTableService.saveBatch(productFamilyMachineTables);
    }
}
