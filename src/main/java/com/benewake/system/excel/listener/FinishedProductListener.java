package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.benewake.system.entity.ApsFinishedProductBasicData;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelFinishedProductTemplate;
import com.benewake.system.excel.transfer.ExcelFinishedProductToPo;
import com.benewake.system.service.ApsFinishedProductBasicDataService;

import java.util.ArrayList;
import java.util.List;

public class FinishedProductListener extends AnalysisEventListener<ExcelFinishedProductTemplate> {

    private final List<ApsFinishedProductBasicData> finishedProductBasicData = new ArrayList<>();

    private final ExcelFinishedProductToPo excelFinishedProductToPo;

    private final Integer type;

    private final ApsFinishedProductBasicDataService finishedProductBasicDataService;

    public FinishedProductListener(ExcelFinishedProductToPo excelFinishedProductToPo, Integer type, ApsFinishedProductBasicDataService finishedProductBasicDataService) {
        this.excelFinishedProductToPo = excelFinishedProductToPo;
        this.type = type;
        this.finishedProductBasicDataService = finishedProductBasicDataService;
    }

    @Override
    public void invoke(ExcelFinishedProductTemplate data, AnalysisContext context) {
        ApsFinishedProductBasicData convert = excelFinishedProductToPo.convert(data);
        finishedProductBasicData.add(convert);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (type == ExcelOperationEnum.OVERRIDE.getCode()) {
            finishedProductBasicDataService.remove(null);
        }
        finishedProductBasicDataService.saveBatch(finishedProductBasicData);
    }
}
