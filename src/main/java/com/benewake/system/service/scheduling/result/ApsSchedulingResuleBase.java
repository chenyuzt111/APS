package com.benewake.system.service.scheduling.result;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsTableVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

public interface ApsSchedulingResuleBase {


    default Integer getApsTableVersion(Integer code ,ApsTableVersionService apsTableVersionService) {
        LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsTableVersionLambdaQueryWrapper.eq(ApsTableVersion::getTableId, code)
                .eq(ApsTableVersion::getState, TableVersionState.SUCCESS.getCode())
                .orderByDesc(ApsTableVersion::getVersionNumber)
                .last("limit 1");

        ApsTableVersion one = apsTableVersionService.getOne(apsTableVersionLambdaQueryWrapper);
        if (one == null || one.getTableVersion() == null) {
            throw new BeneWakeException("还未有排程数据");
        }
        return one.getTableVersion();
    }

    default Integer getMaxVersionAndSave() {
        Integer curVersion = null;
        try {
            QueryWrapper<Object> objectQueryWrapper = new QueryWrapper<>();
            objectQueryWrapper.orderByDesc("version")
                    .last("limit 1")
                    .select("version");

            IService iService = (IService) this;
            Object iServiceOne = iService.getOne(objectQueryWrapper);
            curVersion = 0;
            if (iServiceOne != null) {
                //拿出最大版本
                Class<?> aClass = iServiceOne.getClass();
                Field version = aClass.getDeclaredField("version");
                version.setAccessible(true);
                Integer o = (Integer) version.get(iService);
                if (o == null) {
                    curVersion = 1;
                } else {
                    curVersion = o + 1;
                }
                version.setAccessible(false);
                UpdateWrapper<Object> objectUpdateWrapper = new UpdateWrapper<>();
                objectUpdateWrapper.isNull("version").set("version" ,curVersion);
                iService.update(objectUpdateWrapper);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return curVersion;
    }

}
