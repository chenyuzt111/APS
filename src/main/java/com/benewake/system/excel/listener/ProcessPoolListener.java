package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.excel.entity.ExcelProcessNamePoolTemplate;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.utils.ExcelUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ProcessPoolListener extends AnalysisEventListener<ExcelProcessNamePoolTemplate> {

    private final ApsProcessNamePoolService apsProcessNamePoolService;

    private final Integer type;

    private final List<String> head = Collections.singletonList("工序名称");

    public ProcessPoolListener(ApsProcessNamePoolService apsProcessNamePoolService, Integer type) {
        this.apsProcessNamePoolService = apsProcessNamePoolService;
        this.type = type;
    }

    //员工集合
    private List<ExcelProcessNamePoolTemplate> excelProcessNamePools = new ArrayList<>();


    @Override
    public void invoke(ExcelProcessNamePoolTemplate data, AnalysisContext context) {
        excelProcessNamePools.add(data);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        ExcelUtil.validateHeadMap(headMap ,head);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("工序命名池解析完成" + excelProcessNamePools.size() + new Date());
        List<ApsProcessNamePool> excelProcessNamePools = this.excelProcessNamePools.stream().map(x -> {
            ApsProcessNamePool apsProcessNamePool = new ApsProcessNamePool();
            apsProcessNamePool.setProcessName(x.getProcessName());
            return apsProcessNamePool;
        }).collect(Collectors.toList());
        List<ApsProcessNamePool> aprProcessNamePools = apsProcessNamePoolService.getBaseMapper().selectList(null);
        // 从数据库中获取所有现有的工序命名池记录
        List<String> processNames = aprProcessNamePools.stream().map(ApsProcessNamePool::getProcessName).collect(Collectors.toList());
        if (type == ExcelOperationEnum.OVERRIDE.getCode()) {
            //全量更新
            List<String> excelProcessName = excelProcessNamePools
                    .stream()
                    .map(ApsProcessNamePool::getProcessName)
                    .collect(Collectors.toList());
            //获取当前需要删除的
            List<Integer> deleteIds = aprProcessNamePools.stream()
                    .filter(x -> !excelProcessName.contains(x.getProcessName()))
                    .map(ApsProcessNamePool::getId).collect(Collectors.toList());
            apsProcessNamePoolService.removeBatchByIds(deleteIds);
        }
        // 过滤出不在processNamePools中存在的apsProcessNamePools元素
        excelProcessNamePools = excelProcessNamePools.stream()
                .filter(x -> !processNames.contains(x.getProcessName())
                ).collect(Collectors.toList());
        // 保存那些在数据库中不存在的记录
        apsProcessNamePoolService.saveBatch(excelProcessNamePools);

    }
}
