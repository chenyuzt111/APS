package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.service.InterfaceDataService;
import com.benewake.system.service.KingdeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InterfaceDataServiceImpl implements InterfaceDataService {


    @Autowired
    Map<String, KingdeeService> kingdeeService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    final String INTERFACEDATA_KEY = "interfaceData:";

    @Override
    public Boolean updateData(List<Integer> ids) {
        try {
            ids.forEach(code -> {
                String serviceName = InterfaceDataType.serviceNameOfCode(code);
                Object kingdeeData = null;
                if (StringUtils.isNotBlank(serviceName)) {
                    //根据serviceName取出对应的Service执行具体的查询策略
                    kingdeeData = kingdeeService.get(serviceName).selectAll();
                }
                assert kingdeeData != null;
                redisTemplate.opsForSet().add(INTERFACEDATA_KEY + code, kingdeeData, 30, TimeUnit.MINUTES);
            });
            return Boolean.TRUE;
        } catch (Exception e) {
            Set<String> redisKeys = ids.stream().map(x ->
                    INTERFACEDATA_KEY + x
            ).collect(Collectors.toSet());
            //如果失败就删除之前添加的所有数据 下一次重新添加
            redisTemplate.delete(redisKeys);
            log.error("更新接口数据" + LocalDateTime.now() + "ExceptionMessage" + e.getMessage());
            return Boolean.FALSE;
        }
    }

}
