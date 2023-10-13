package com.benewake.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

public interface KingdeeService<T> {

    Boolean updateDataVersions() throws Exception;

    default Integer getMaxVersionIncr() throws NoSuchFieldException, IllegalAccessException {
        QueryWrapper<Object> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.orderByDesc("version")
                .last("limit 1");
        IService apsImmediatelyInventoryServices = (IService) this;
        Object one = apsImmediatelyInventoryServices.getOne(objectQueryWrapper);
        Integer ver = 0;
        if (one != null) {
            Field[] fields = one.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().equals("version")) {
                    ver = (Integer) field.get(one);
                    break;
                }
                field.setAccessible(false);
            }
        }
        return ver + 1;
    }
}
