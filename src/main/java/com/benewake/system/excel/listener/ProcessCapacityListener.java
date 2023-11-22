package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.benewake.system.entity.ApsFinishedProductBasicData;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelProcessCapacityTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsFinishedProductBasicDataService;
import com.benewake.system.service.ApsProcessCapacityService;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ProcessCapacityListener extends AnalysisEventListener<ExcelProcessCapacityTemplate> {

    private final ApsProcessCapacityService processCapacityService;
    private final Integer type;
    private final List<String> head = Arrays.asList("所属工序", "产品族", "序号", "工序名称", "切换时间", "包装方式", "标准工时", "人数MAX", "人数MIN");
    private final Map<String, Integer> processNameMap;
    private final List<ApsProcessCapacity> excelProcessCapacities = new ArrayList<>();
    private final StringBuilder processNameError = new StringBuilder();
    private final StringBuilder errorMassage = new StringBuilder();
    private final Set<String> productFamilySet;

    public ProcessCapacityListener(ApsProcessCapacityService processCapacityService, ApsProcessNamePoolService processNamePoolService,
                                   Integer type, ApsFinishedProductBasicDataService apsFinishedProductBasicDataService) {
        this.processCapacityService = processCapacityService;
        this.type = type;
        List<ApsFinishedProductBasicData> apsFinishedProductBasicData = apsFinishedProductBasicDataService.getBaseMapper().selectList(null);
        productFamilySet = apsFinishedProductBasicData.stream().map(ApsFinishedProductBasicData::getFProductFamily).collect(Collectors.toSet());
        processNameMap = processNamePoolService.getBaseMapper().selectList(null)
                .stream()
                .collect(Collectors.toMap(ApsProcessNamePool::getProcessName, ApsProcessNamePool::getId));
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        validateHeadMap(headMap);
    }

    @Override
    public void invoke(ExcelProcessCapacityTemplate data, AnalysisContext context) {
        int rowIndex = context.readRowHolder().getRowIndex();
        ApsProcessCapacity apsProcessCapacity = createApsProcessCapacity(data, rowIndex);
        if (apsProcessCapacity != null)
            excelProcessCapacities.add(apsProcessCapacity);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        handleAfterAnalysed();
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        handleException(exception, context);
    }

    private void validateHeadMap(Map<Integer, String> headMap) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < head.size(); i++) {
            String h = headMap.get(i);
            if (h == null || !h.equals(head.get(i))) {
                res.append("第").append(i + 1).append("列的列名不符合条件，应改为:").append(head.get(i)).append("\n");
            }
        }
        if (StringUtils.isNotEmpty(res.toString())) {
            log.info("工序与产能导入" + res + new Date());
            throw new BeneWakeException(res.toString());
        }
    }

    private ApsProcessCapacity createApsProcessCapacity(ExcelProcessCapacityTemplate data, int rowIndex) {
        ApsProcessCapacity apsProcessCapacity = new ApsProcessCapacity();
        if (StringUtils.isEmpty(data.getBelongingProcess())) {
            errorMassage.append("第").append(rowIndex).append("行所属工序不能为空、");
            return null;
        }

        if (StringUtils.isEmpty(data.getProductFamily())) {
            errorMassage.append("第").append(rowIndex).append("行产品族不能为空、");
            return null;
        }

        if (!productFamilySet.contains(data.getProductFamily())) {
            errorMassage.append("第").append(rowIndex).append("行产品族不存在、");
            return null;
        }

        apsProcessCapacity.setBelongingProcess(data.getBelongingProcess());
        Integer processId = processNameMap.get(data.getProcessName());
        if (processId == null) {
            processNameError.append("第").append(rowIndex).append("行").append(data.getProcessName()).append("、");
            return null;
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
        return apsProcessCapacity;
    }

    private void handleAfterAnalysed() {
        if (StringUtils.isNotEmpty(errorMassage.toString())) {
            throw new BeneWakeException(errorMassage.toString());
        }
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

    private void handleException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException dataConvertException = (ExcelDataConvertException) exception;
            String exceptionMessage = "第 " + dataConvertException.getRowIndex() + " 行，第 " +
                    dataConvertException.getColumnIndex() + " 列的数据转换异常：";
            log.error(exceptionMessage + dataConvertException.getCellData());
            throw new BeneWakeException(exceptionMessage);
        } else {
            super.onException(exception, context);
        }
    }
}
