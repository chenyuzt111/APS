package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisStopException;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelProcessNamePool;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsProcessNamePoolService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ProcessPoolListener extends AnalysisEventListener<ExcelProcessNamePool> {

    private final ApsProcessNamePoolService apsProcessNamePoolService;

    private Integer type;

    public ProcessPoolListener(ApsProcessNamePoolService apsProcessNamePoolService , Integer type) {
        this.apsProcessNamePoolService = apsProcessNamePoolService;
        this.type = type;
    }
    //员工集合
    private List<ExcelProcessNamePool> excelProcessNamePools = new ArrayList<>();


    @Override
    public void invoke(ExcelProcessNamePool data, AnalysisContext context) {
        excelProcessNamePools.add(data);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        String h;
        if((h = headMap.get(0))==null || !h.equals("工序名称")){
            throw new BeneWakeException("第一列名称应该为 工序名称");
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("工序命名池解析完成" + excelProcessNamePools.size() + new Date());
        List<ApsProcessNamePool> apsProcessNamePools = excelProcessNamePools.stream().map(x -> {
            ApsProcessNamePool apsProcessNamePool = new ApsProcessNamePool();
            apsProcessNamePool.setProcessName(x.getProcessName());
            return apsProcessNamePool;
        }).collect(Collectors.toList());
        if (type == ExcelOperationEnum.OVERRIDE.getCode()) {
            //覆盖
            apsProcessNamePoolService.remove(null);
            apsProcessNamePoolService.saveBatch(apsProcessNamePools);
        } else {
            List<ApsProcessNamePool> processNamePools = apsProcessNamePoolService.getBaseMapper().selectList(null);
            apsProcessNamePools.removeIf(processNamePools::contains);
            apsProcessNamePoolService.saveBatch(apsProcessNamePools);
        }
    }

}
