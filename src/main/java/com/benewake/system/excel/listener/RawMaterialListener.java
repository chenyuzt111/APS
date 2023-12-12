package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.benewake.system.entity.ApsRawMaterialBasicData;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelRawMaterialBasicDataTemplate;
import com.benewake.system.excel.transfer.ExcelRawMaterialToPo;
import com.benewake.system.service.ApsRawMaterialBasicDataService;

import java.util.ArrayList;
import java.util.List;

public class RawMaterialListener extends AnalysisEventListener<ExcelRawMaterialBasicDataTemplate> {

    private final List<ApsRawMaterialBasicData> rawMaterialBasicDatas = new ArrayList<>();

    private final ExcelRawMaterialToPo excelRawMaterialToPo;

    private final Integer type;

    private final ApsRawMaterialBasicDataService rawMaterialBasicDataService;

    public RawMaterialListener(ExcelRawMaterialToPo excelRawMaterialToPo, Integer type, ApsRawMaterialBasicDataService rawMaterialBasicDataService) {
        this.excelRawMaterialToPo = excelRawMaterialToPo;
        this.type = type;
        this.rawMaterialBasicDataService = rawMaterialBasicDataService;
    }

    @Override
    public void invoke(ExcelRawMaterialBasicDataTemplate data, AnalysisContext context) {
        ApsRawMaterialBasicData convert = excelRawMaterialToPo.convert(data);
        rawMaterialBasicDatas.add(convert);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (type == ExcelOperationEnum.OVERRIDE.getCode()) {
            rawMaterialBasicDataService.remove(null);
        }
        rawMaterialBasicDataService.saveBatch(rawMaterialBasicDatas);
    }
}
