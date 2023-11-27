package com.benewake.system.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.service.ApsIntfaceDataServiceBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class InterfaceDataListener extends AnalysisEventListener<Object> {

    private final ApsIntfaceDataServiceBase base;

    private final Class classs;

    private List<Object> list = new ArrayList<>();

    private final Field[] declaredFields;

    private final Integer version;

    public InterfaceDataListener(Integer code, ApsIntfaceDataServiceBase base, Integer type) {
        this.base = base;
        InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(code);
        this.classs = interfaceDataType.getClasss();
        declaredFields = classs.getDeclaredFields();
        Integer maxVersionIncr = base.getMaxVersionIncr();
        version = type == ExcelOperationEnum.OVERRIDE.getCode() ? maxVersionIncr : maxVersionIncr - 1;
    }


    @Override
    public void invoke(Object data, AnalysisContext context) {
        try {
            Object transferMapToPo = transferMapToPo((Map) data);
            list.add(transferMapToPo);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        long l = System.currentTimeMillis();
        ((IService) base).saveBatch(list);
        long l1 = System.currentTimeMillis();
        System.err.println("转换------" + sum);
    }

    long sum = 0;

    public Object transferMapToPo(Map data) throws InstantiationException, IllegalAccessException {
        long l = System.currentTimeMillis();
        Object newInstance = classs.newInstance();
        for (int i = 1; i < declaredFields.length - 2; i++) {
            Object value = data.get(i - 1);
            Field declaredField = declaredFields[i];
            declaredField.setAccessible(true);
            declaredField.set(newInstance, value);
            declaredField.setAccessible(false);
        }
        declaredFields[declaredFields.length - 2].setAccessible(true);
        declaredFields[declaredFields.length - 2].set(newInstance, version);
        declaredFields[declaredFields.length - 2].setAccessible(false);
        long l1 = System.currentTimeMillis();
        sum = sum + l1 - l;
        return newInstance;
    }
}
