package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelProcessCapacity;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsProcessCapacityService;
import com.benewake.system.service.ApsProcessNamePoolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ProcessCapacityListener extends AnalysisEventListener<ExcelProcessCapacity> {

    private final ApsProcessCapacityService processCapacityService;

    private Integer type;

    private final static List<String> head = new ArrayList<>();

    private final Map<String, Integer> processNameMap;

    //    private final ProcessCapacityExcelToPo processCapacityExcelToPo;
    private List<ApsProcessCapacity> excelProcessCapacities = new ArrayList<>();

    private StringBuilder processNameError;

    static {
        head.add("所属工序");
        head.add("工序名称");
        head.add("序号");
        head.add("产品族");
        head.add("包装方式");
        head.add("标准工时");
        head.add("切换时间");
        head.add("人数MAX");
        head.add("人数MIN");
    }

    public ProcessCapacityListener(ApsProcessCapacityService processCapacityService, ApsProcessNamePoolService processNamePoolService, Integer type) {
        this.processCapacityService = processCapacityService;
        this.type = type;
//        this.processCapacityExcelToPo = processCapacityExcelToPo;
        processNameError = new StringBuilder("");
        List<ApsProcessNamePool> apsProcessNamePools = processNamePoolService.getBaseMapper().selectList(null);
        processNameMap = apsProcessNamePools.stream().collect(Collectors.toMap(ApsProcessNamePool::getProcessName, ApsProcessNamePool::getId));
    }


    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        // 判断列名是否一致
        StringBuilder res = new StringBuilder("");
        for (int i = 0; i < head.size(); i++) {
            String h;
            if ((h = headMap.get(i)) == null || !h.equals(head.get(i))) {
                res.append("第").append(i + 1).append("列的列名不符合条件，应改为:").append(head.get(i)).append("\n");
            }
        }
        if (StringUtils.isNotEmpty(res.toString())) {
            log.info("工序与产能导入" + res + new Date());
            throw new BeneWakeException(res.toString());
        }
    }

    @Override
    public void invoke(ExcelProcessCapacity data, AnalysisContext context) {
        ApsProcessCapacity apsProcessCapacity = new ApsProcessCapacity();
        apsProcessCapacity.setBelongingProcess(data.getBelongingProcess());
        Integer processId = processNameMap.get(data.getProcessName());
        if (processId == null) {
            processNameError.append(data.getProcessName()).append("、");
            return;
        }
        apsProcessCapacity.setProcessId(processId);
        apsProcessCapacity.setProcessNumber(data.getProcessNumber());
        apsProcessCapacity.setProductFamily(data.getProductFamily());
        apsProcessCapacity.setPackagingMethod(data.getPackagingMethod());
        apsProcessCapacity.setSwitchTime(data.getSwitchTime());
        if (StringUtils.isNotEmpty(data.getStandardTime())) {
            apsProcessCapacity.setStandardTime(new BigDecimal(data.getStandardTime()));
        }
        apsProcessCapacity.setMaxPersonnel(data.getMaxPersonnel());
        apsProcessCapacity.setMinPersonnel(data.getMinPersonnel());
        excelProcessCapacities.add(apsProcessCapacity);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (StringUtils.isNotEmpty(processNameError.toString())) {
            throw new BeneWakeException(processNameError.append("在工序命名池中不存在！").toString());
        }

        if (type.equals(ExcelOperationEnum.OVERRIDE.getCode())) {
            processCapacityService.remove(null);
            processCapacityService.saveBatch(excelProcessCapacities);
        } else {
            List<ApsProcessCapacity> processCapacityList = processCapacityService.getBaseMapper().selectList(null);
            List<String> productFamily = processCapacityList.stream()
                    .map(ApsProcessCapacity::getProductFamily)
                    .distinct()
                    .collect(Collectors.toList());
            List<String> addProductFamily = excelProcessCapacities.stream()
                    .map(ApsProcessCapacity::getProductFamily)
                    .distinct()
                    .collect(Collectors.toList());
            List<String> duplicates = addProductFamily.stream()
                    .filter(productFamily::contains)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(duplicates)) {
                String existFamily = String.join("、", duplicates);
                throw new BeneWakeException(existFamily + "已经存在了");
            }
            processCapacityService.saveBatch(excelProcessCapacities);
        }
    }
}
