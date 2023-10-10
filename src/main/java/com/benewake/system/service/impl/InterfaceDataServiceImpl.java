package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.InterfaceDataService;
import com.benewake.system.service.KingdeeService;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.threadpool.BenewakeExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Time;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class InterfaceDataServiceImpl implements InterfaceDataService {

    @Autowired
    private Map<String, KingdeeService> kingdeeServiceMap;

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HostHolder hostHolder;

    static volatile Integer maxVersion;

    //用户级别的锁
    final String SCHEDULING_LOCK_KEY = "Scheduling::user:lock";

    final String INTERFACE_DATA_LOCK_KEY = "Interface::Data";


    final String INTERFACE_DATA_USERNAME_KEY = "Interface::DataUserName";


    @Override
    public Boolean updateData(List<Integer> ids) throws Exception {
        RLock schedulingUserLock = redisson.getLock(SCHEDULING_LOCK_KEY);
        RLock interfaceDataLock = redisson.getLock(INTERFACE_DATA_LOCK_KEY);

        if (schedulingUserLock.tryLock()) {
            if (interfaceDataLock.tryLock()) {
                try {
                    redisTemplate.opsForValue().set(INTERFACE_DATA_USERNAME_KEY, hostHolder.getUser().getUsername());
                    Thread.sleep(1000);
                    apsTableVersionService.incrVersions();
                    maxVersion = apsTableVersionService.getMaxVersion();
                    CountDownLatch countDownLatch = new CountDownLatch(ids.size());
                    for (Integer code : ids) {
                        BenewakeExecutor.execute(() -> {
                            try {
                                String serviceName = InterfaceDataType.serviceNameOfCode(code);
                                if (StringUtils.isBlank(serviceName)) {
                                    throw new BeneWakeException("id为：" + code + "未找到对应service"); // 如果 serviceName 为空，跳过当前循环
                                }
                                KingdeeService service = kingdeeServiceMap.get(serviceName);
                                if (service == null) {
                                    throw new BeneWakeException(serviceName + "未找到");// 如果未找到对应的服务，跳过当前循环
                                }
                                Boolean res = service.updateDataVersions();
                                if (!res) {
                                    throw new BeneWakeException(serviceName + "数据库更新异常");
                                }
                            } catch (Exception e) {
                                handleUpdateTableState(TableVersionState.INVALID.getCode());
                                e.printStackTrace();
                                throw new BeneWakeException("更新异常");
                            } finally {
                                countDownLatch.countDown();
                            }
                        });
                    }
                    countDownLatch.await();
                    return Boolean.TRUE;
                } finally {
                    interfaceDataLock.unlock();
                }
            }
        }  else {
            throw new BeneWakeException("其他人正在操作！");
        }
        return null;
    }

    private void handleUpdateTableState(int code) {
        LambdaUpdateWrapper<ApsTableVersion> apsTableVersionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        apsTableVersionLambdaUpdateWrapper.eq(ApsTableVersion::getVersionNumber, maxVersion);
        apsTableVersionLambdaUpdateWrapper.set(ApsTableVersion::getState, code);
        apsTableVersionService.update(apsTableVersionLambdaUpdateWrapper);
    }
}
