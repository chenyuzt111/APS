package com.benewake.system.controller.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.service.ApsIntfaceDataServiceBase;
import com.benewake.system.service.ApsTableVersionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InterfaceDataRinseTask {

    @Autowired
    private Map<String, ApsIntfaceDataServiceBase> kingdeeServiceMap;

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    //todo 剩余aps_table_version表需要同一清洗
    //每天0点0分0秒定时执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanTasks() {
        log.info(LocalDateTime.now() + "-- 数据清洗");
        Set<Map.Entry<String, ApsIntfaceDataServiceBase>> entries = kingdeeServiceMap.entrySet();
        for (Map.Entry<String, ApsIntfaceDataServiceBase> entry : entries) {
            try {
                String k = entry.getKey();
                ApsIntfaceDataServiceBase value = entry.getValue();
                //取出最新的一条为即时库存不删除
                IService iService = (IService) value;
                QueryWrapper<Object> objectQueryWrapper = new QueryWrapper<>();
                objectQueryWrapper.orderByDesc("version").last(" limit 1");
                Object one = iService.getOne(objectQueryWrapper);
                if (one == null) {
                    //如果当前连一条数据都没有那么也没有继续的必要了
                    continue;
                }
                Field version = one.getClass().getDeclaredField("version");
                //即时库存版本 最新版本
                version.setAccessible(true);
                Integer inventoryVersion = (Integer) version.get(one);
                version.setAccessible(false);
                int code = InterfaceDataType.getCodeByServiceName(k);
                LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
                apsTableVersionLambdaQueryWrapper.eq(ApsTableVersion::getTableId, code)
                        .eq(ApsTableVersion::getState, TableVersionState.SUCCESS.getCode())
                        .orderByDesc(ApsTableVersion::getVersionNumber)
                        .last(" limit 5");

                List<ApsTableVersion> apsTableVersions = apsTableVersionService.getBaseMapper().selectList(apsTableVersionLambdaQueryWrapper);
                objectQueryWrapper = new QueryWrapper<>();
                List<Integer> tableVersion = null;
                if (CollectionUtils.isNotEmpty(apsTableVersions)) {
                    tableVersion = apsTableVersions.stream().map(ApsTableVersion::getTableVersion).distinct().collect(Collectors.toList());
                }
                tableVersion.add(inventoryVersion);
                objectQueryWrapper.notIn("version", tableVersion);
                iService.remove(objectQueryWrapper);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
