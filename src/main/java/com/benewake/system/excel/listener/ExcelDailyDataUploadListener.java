package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.benewake.system.entity.ApsDailyDataUpload;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelDailyDataUploadTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsDailyDataUploadService;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.utils.BenewakeStringUtils;
import com.benewake.system.utils.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ExcelDailyDataUploadListener extends AnalysisEventListener<ExcelDailyDataUploadTemplate> {

    private final ApsDailyDataUploadService dailyDataUploadService;
    private final ArrayList<ApsDailyDataUpload> apsDailyDataUploads = new ArrayList<>();
    private final Integer type;
    private final Map<String, Integer> processNameToId;
    private final StringBuilder processNameError = new StringBuilder();
    //    private final StringBuilder errorMassage = new StringBuilder();
    private final List<String> head = Arrays.asList("订单编号", "物料编码", "工序名称", "总数量", "完成数量", "产能(秒/台/人)", "剩余数量", "剩余产能");

    public ExcelDailyDataUploadListener(ApsDailyDataUploadService dailyDataUploadService, Integer type, ApsProcessNamePoolService processNamePoolService) {
        this.dailyDataUploadService = dailyDataUploadService;
        this.type = type;
        List<ApsProcessNamePool> processNamePools = processNamePoolService.getBaseMapper().selectList(null);
        processNameToId = processNamePools.stream().collect(Collectors.toMap(ApsProcessNamePool::getProcessName, ApsProcessNamePool::getId));
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        ExcelUtil.validateHeadMap(headMap, head);
    }

    @Override
    public void invoke(ExcelDailyDataUploadTemplate data, AnalysisContext context) {
        ApsDailyDataUpload apsDailyDataUpload = dailyDateUploadExcelToPo(data);
        if (apsDailyDataUpload != null) {
            apsDailyDataUploads.add(apsDailyDataUpload);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        handleAfterAnalysed();
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        handleException(exception, context);
    }

    private void handleAfterAnalysed() {
        if (BenewakeStringUtils.isNotEmpty(processNameError.toString())) {
            throw new BeneWakeException(processNameError.append("在工序命名池中不存在！").toString());
        }

        if (type.equals(ExcelOperationEnum.OVERRIDE.getCode())) {
            dailyDataUploadService.remove(null);
        }
        dailyDataUploadService.saveBatch(apsDailyDataUploads);
    }

    private ApsDailyDataUpload dailyDateUploadExcelToPo(ExcelDailyDataUploadTemplate data) {
        ApsDailyDataUpload apsDailyDataUpload = new ApsDailyDataUpload();
        String processName = data.getProcessName();
        if (StringUtils.isNotEmpty(processName)) {
            Integer processId = processNameToId.get(processName);
            if (processId == null) {
                processNameError.append(processName).append("、");
                return null;
            }
            apsDailyDataUpload.setFProcessId(processId);
        }
        apsDailyDataUpload.setFOrderNumber(data.getOrderNumber());
        apsDailyDataUpload.setFMaterialCode(data.getMaterialCode());
        apsDailyDataUpload.setFTotalQuantity(String.valueOf(data.getTotalQuantity()));
        apsDailyDataUpload.setFCompletedQuantity(String.valueOf(data.getCompletedQuantity()));
        apsDailyDataUpload.setFCapacityPsPuPp(String.valueOf(data.getCapacityPsPuPp()));
        apsDailyDataUpload.setFRemainingQuantity(String.valueOf(data.getRemainingQuantity()));
        apsDailyDataUpload.setFRemainingCapacity(String.valueOf(data.getRemainingCapacity()));
        return apsDailyDataUpload;
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
