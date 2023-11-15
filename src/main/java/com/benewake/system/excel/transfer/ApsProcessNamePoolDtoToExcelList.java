package com.benewake.system.excel.transfer;


import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.excel.entity.ExcelProcessNamePool;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class ApsProcessNamePoolDtoToExcelList {

    public List<ExcelProcessNamePool> convert(List<ApsProcessNamePool> object) {
        if (CollectionUtils.isEmpty(object)) {
            return null;
        }
        AtomicReference<Integer> number = new AtomicReference<>(1);
        List<ExcelProcessNamePool> excelProcessNamePools = object.stream().map(x -> {
            ExcelProcessNamePool excelProcessNamePool = new ExcelProcessNamePool();
            excelProcessNamePool.setNumber(number.getAndSet(number.get() + 1));
            excelProcessNamePool.setProcessName(x.getProcessName());
            return excelProcessNamePool;
        }).collect(Collectors.toList());

        return excelProcessNamePools;
    }
}
