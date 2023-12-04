package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.benewake.system.entity.ApsSemiFinishedBasicData;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelSemiFinishedTemplate;
import com.benewake.system.excel.transfer.ExcelSemiFinishedToPo;
import com.benewake.system.service.ApsSemiFinishedBasicDataService;

import java.util.ArrayList;
import java.util.List;

public class ExcelSemiFinishedListener extends AnalysisEventListener<ExcelSemiFinishedTemplate> {

    private final List<ApsSemiFinishedBasicData> semiFinishedBasicData = new ArrayList<>();

    private final ApsSemiFinishedBasicDataService semiFinishedBasicDataService;

    private final Integer type;

    private final ExcelSemiFinishedToPo excelSemiFinishedToPo;

    public ExcelSemiFinishedListener(ExcelSemiFinishedToPo excelSemiFinishedToPo ,Integer type ,ApsSemiFinishedBasicDataService semiFinishedBasicDataService) {
        this.semiFinishedBasicDataService = semiFinishedBasicDataService;
        this.type = type;
        this.excelSemiFinishedToPo = excelSemiFinishedToPo;
    }

    @Override
    public void invoke(ExcelSemiFinishedTemplate data, AnalysisContext context) {
        ApsSemiFinishedBasicData convert = excelSemiFinishedToPo.convert(data);
        semiFinishedBasicData.add(convert);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (type == ExcelOperationEnum.OVERRIDE.getCode()) {
            semiFinishedBasicDataService.remove(null);
        }
        semiFinishedBasicDataService.saveBatch(semiFinishedBasicData);
    }
}
