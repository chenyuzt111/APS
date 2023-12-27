package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.benewake.system.entity.*;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelProcessCapacityTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsProcessSchemeMapper;
import com.benewake.system.service.ApsFinishedProductBasicDataService;
import com.benewake.system.service.ApsProcessCapacityService;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.service.ApsProductFamilyProcessSchemeManagementService;
import com.benewake.system.utils.BenewakeStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ProcessCapacityListener extends AnalysisEventListener<ExcelProcessCapacityTemplate> {

    private final ApsProcessCapacityService processCapacityService;
    private final Integer type;
    private final Map<String, Integer> processNameMap;
    private final List<ApsProcessCapacity> apsProcessCapacities = new ArrayList<>();
    private final StringBuilder processNameError = new StringBuilder();
    private final StringBuilder errorMassage = new StringBuilder();
    private final Set<String> productFamilySet;
    private final ApsProcessSchemeMapper apsProcessSchemeMapper;
    private final ApsProductFamilyProcessSchemeManagementService managementService;

    public ProcessCapacityListener(ApsProcessCapacityService processCapacityService, ApsProcessNamePoolService processNamePoolService,
                                   Integer type, ApsFinishedProductBasicDataService apsFinishedProductBasicDataService, ApsProcessSchemeMapper apsProcessSchemeMapper, ApsProductFamilyProcessSchemeManagementService managementService) {
        this.processCapacityService = processCapacityService;
        this.type = type;
        this.apsProcessSchemeMapper = apsProcessSchemeMapper;
        this.managementService = managementService;
        List<ApsFinishedProductBasicData> apsFinishedProductBasicData = apsFinishedProductBasicDataService.getBaseMapper().selectList(null);
        productFamilySet = apsFinishedProductBasicData.stream().map(ApsFinishedProductBasicData::getFProductFamily).collect(Collectors.toSet());
        processNameMap = processNamePoolService.getBaseMapper().selectList(null)
                .stream()
                .collect(Collectors.toMap(ApsProcessNamePool::getProcessName, ApsProcessNamePool::getId));
    }


    @Override
    public void invoke(ExcelProcessCapacityTemplate data, AnalysisContext context) {
        int rowIndex = context.readRowHolder().getRowIndex();
        ApsProcessCapacity apsProcessCapacity = createApsProcessCapacity(data, rowIndex);
        if (apsProcessCapacity != null)
            apsProcessCapacities.add(apsProcessCapacity);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        handleAfterAnalysed();
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        handleException(exception, context);
    }


    private ApsProcessCapacity createApsProcessCapacity(ExcelProcessCapacityTemplate data, int rowIndex) {
        ApsProcessCapacity apsProcessCapacity = new ApsProcessCapacity();
        if (BenewakeStringUtils.isEmpty(data.getBelongingProcess())) {
            errorMassage.append("第").append(rowIndex).append("行所属工序不能为空、");
            return null;
        }

        if (BenewakeStringUtils.isEmpty(data.getProductFamily())) {
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
        apsProcessCapacity.setConcurrencyCount(data.getConcurrencyCount());
        apsProcessCapacity.setProductFamily(data.getProductFamily());
        apsProcessCapacity.setPackagingMethod(data.getPackagingMethod());
        apsProcessCapacity.setSwitchTime(data.getSwitchTime());
        if (BenewakeStringUtils.isNotEmpty(data.getStandardTime())) {
            apsProcessCapacity.setStandardTime(new BigDecimal(data.getStandardTime()));
        }
        apsProcessCapacity.setMaxPersonnel(data.getMaxPersonnel());
        apsProcessCapacity.setMinPersonnel(data.getMinPersonnel());
        return apsProcessCapacity;
    }

    private void handleAfterAnalysed() {
        if (BenewakeStringUtils.isNotEmpty(errorMassage.toString())) {
            throw new BeneWakeException(errorMassage.toString());
        }
        if (BenewakeStringUtils.isNotEmpty(processNameError.toString())) {
            throw new BeneWakeException(processNameError.append("在工序命名池中不存在！").toString());
        }

        if (type.equals(ExcelOperationEnum.OVERRIDE.getCode())) {
            //覆盖导入匹配
            List<ApsProcessCapacity> processCapacities = processCapacityService.list();
            if (CollectionUtils.isEmpty(processCapacities)) {
                processCapacityService.saveBatch(apsProcessCapacities);
            }

            Map<String, List<ApsProcessCapacity>> apsProcessCapacityMap = processCapacities.stream()
                    .collect(Collectors.groupingBy(ApsProcessCapacity::getProductFamily));
            Map<String, List<ApsProcessCapacity>> productFamilyMap = apsProcessCapacities.stream()
                    .collect(Collectors.groupingBy(ApsProcessCapacity::getProductFamily));

            ArrayList<ApsProcessCapacity> addList = new ArrayList<>();
            List<ApsProcessCapacity> updateList = new ArrayList<>();
            List<Integer> deleteList = new ArrayList<>();
            List<ApsProcessCapacity> deletes = new ArrayList<>();

            for (Map.Entry<String, List<ApsProcessCapacity>> entry : productFamilyMap.entrySet()) {
                String productFamily = entry.getKey();
                if (!apsProcessCapacityMap.containsKey(productFamily)) {
                    //不存在该产品族 直接保存
                    addList.addAll(entry.getValue());
                } else {
                    //存在判断是否一致匹配工序 将存在的id取出来 进行更新
                    List<ApsProcessCapacity> excelValue = entry.getValue();
                    List<ApsProcessCapacity> dataValue = apsProcessCapacityMap.get(productFamily);
                    // 遍历excelValue
                    for (ApsProcessCapacity excelElement : excelValue) {
                        boolean found = false;
                        // 在dataValue中查找相同的processId
                        for (ApsProcessCapacity dataElement : dataValue) {
                            if (excelElement.getProcessId().equals(dataElement.getProcessId())) {
                                // 找到相同的processId，将dataElement的id设置到excelElement中
                                excelElement.setId(dataElement.getId());
                                updateList.add(excelElement);
                                found = true;
                                break;
                            }
                        }
                        // 如果在dataValue中找不到相同的processId，将excelElement添加到addList
                        if (!found) {
                            addList.add(excelElement);
                        }
                    }
                }
            }

            for (Map.Entry<String, List<ApsProcessCapacity>> entry : apsProcessCapacityMap.entrySet()) {
                List<ApsProcessCapacity> dataValue = entry.getValue();
                List<ApsProcessCapacity> excelValue = productFamilyMap.get(entry.getKey());
                // 遍历dataValue
                for (ApsProcessCapacity dataElement : dataValue) {
                    boolean found = false;
                    // 在excelValue中查找相同的processId
                    if (CollectionUtils.isNotEmpty(excelValue)) {
                        for (ApsProcessCapacity excelElement : excelValue) {
                            if (dataElement.getProcessId().equals(excelElement.getProcessId())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    // 如果在excelValue中找不到相同的processId，将dataElement添加到deleteList
                    if (!found) {
                        deleteList.add(dataElement.getId());
                        deletes.add(dataElement);
                    }
                }
            }

            LambdaQueryWrapper<ApsProcessScheme> schemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            schemeLambdaQueryWrapper.in(ApsProcessScheme::getProcessCapacityId, deleteList);
            List<String> familyList = getFamilyList(addList, deletes);
            setSchemeStateFalse(familyList);
            deleteOptimal(familyList);

            apsProcessSchemeMapper.delete(schemeLambdaQueryWrapper);
            processCapacityService.saveBatch(addList);
            processCapacityService.updateBatchById(updateList);
            processCapacityService.removeBatchByIds(deleteList);
        } else {
            List<ApsProcessCapacity> processCapacityList = processCapacityService.getBaseMapper().selectList(null);
            List<String> productFamily = processCapacityList.stream()
                    .map(ApsProcessCapacity::getProductFamily)
                    .distinct()
                    .collect(Collectors.toList());
            List<String> addProductFamily = apsProcessCapacities.stream()
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
            processCapacityService.saveBatch(apsProcessCapacities);
        }

    }

    private List<String> getFamilyList(ArrayList<ApsProcessCapacity> addList, List<ApsProcessCapacity> deletes) {
        List<String> familyList = addList.stream().map(ApsProcessCapacity::getProductFamily)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
        familyList.addAll(deletes.stream().map(ApsProcessCapacity::getProductFamily)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList()));
        return familyList;
    }

    private void setSchemeStateFalse(List<String> productFamily) {
        if (CollectionUtils.isEmpty(productFamily)) {
            return;
        }
        LambdaUpdateWrapper<ApsProcessScheme> schemeUpdateWrapper = new LambdaUpdateWrapper<>();
        for (String family : productFamily) {
            schemeUpdateWrapper.or(i -> i.likeRight(ApsProcessScheme::getCurrentProcessScheme, family));
        }
        schemeUpdateWrapper.eq(ApsProcessScheme::getState, true)
                .set(ApsProcessScheme::getState, false);
        apsProcessSchemeMapper.update(null, schemeUpdateWrapper);
    }

    private void deleteOptimal(List<String> productFamily) {
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApsProductFamilyProcessSchemeManagement::getProductFamily,
                productFamily);
        managementService.remove(queryWrapper);
    }

    private void handleException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException dataConvertException = (ExcelDataConvertException) exception;
            String exceptionMessage = "第 " + dataConvertException.getRowIndex() + " 行，第 " +
                    dataConvertException.getColumnIndex() + " 列的数据格式或者类型不正确：";
            log.error(exceptionMessage + dataConvertException.getCellData());
            throw new BeneWakeException(exceptionMessage);
        } else {
            super.onException(exception, context);
        }
    }
}
