package com.benewake.system.service.impl;

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

    @Resource
    private PythonBase schedulingPythonService;

    @Resource
    private PythonBase integrityCheckerPythonService;

    @Autowired
    private RedissonClient redisson;

    final String INTERFACE_DATA_LOCK_KEY = "Interface::Data";
    @Override
    public void startScheduling() {

        schedulingPythonService.startAsync();
    }

    @Override
    public void integrityChecker() {
        integrityCheckerPythonService.start();
    }

}
