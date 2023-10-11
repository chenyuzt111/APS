package com.benewake.system.service.impl;

import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.PythonService;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.python.PythonBase;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PythonServiceImpl implements PythonService {

    @Autowired
    private HostHolder hostHolder;

    @Resource
    private PythonBase schedulingPythonService;

    @Resource
    private PythonBase integrityCheckerPythonService;

    @Autowired
    private ApsTableVersionService apsTableVersionService;
    @Override
    public void startScheduling() {
        Integer maxVersionState = apsTableVersionService.getMaxVersionState();
        if (maxVersionState.equals(TableVersionState.INTEGRIT_CHECKER.getCode())) {
            //读取最新数据 是否为已经完整性检查的状态
            schedulingPythonService.startAsync(hostHolder.getUser());
        } else {
            throw new BeneWakeException("当前版本状态未处于完整性检查完成状态");
        }
    }

    @Override
    public void integrityChecker() {
        Integer maxVersionState = apsTableVersionService.getMaxVersionState();
        if (maxVersionState.equals(TableVersionState.EDITING.getCode())) {
            integrityCheckerPythonService.start();
        } else {
            throw new BeneWakeException("当前版本状态未处于编辑状态");
        }
    }

}
