package com.benewake.system.aspect;

import com.benewake.system.annotation.Scheduling;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.redis.DistributedLock;
import com.benewake.system.utils.HostHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.benewake.system.redis.SchedulingLockKey.*;

@Aspect
@Component
public class SchedulingAspect {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;


    @Around("@annotation(scheduling)")
    public Object doBefore(ProceedingJoinPoint point, Scheduling scheduling) throws Throwable {

        if (getUserLock()) {
            RLock interfaceDataLock = redisson.getLock(SCHEDULING_DATA_LOCK_KEY);
            if (!distributedLock.acquireLock(SCHEDULING_ING_LOCK_KEY, hostHolder.getUser().getUsername())) {
                throw new BeneWakeException("正在排程！");
            }
            distributedLock.releaseLock(SCHEDULING_ING_LOCK_KEY, hostHolder.getUser().getUsername());
            if (interfaceDataLock.tryLock()) {
                try {
                    redisTemplate.opsForValue().set(SCHEDULING_DATA_LOCK_KEY_STATE, String.valueOf(scheduling.tableExecuteState().getCode()));
                    return point.proceed();
                } finally {
                    interfaceDataLock.unlock();
                }
            } else {
                String code = redisTemplate.opsForValue().get(SCHEDULING_DATA_LOCK_KEY_STATE);
                assert code != null;
                TableVersionState tableVersionState = TableVersionState.valueOf(Integer.parseInt(code));
                throw new BeneWakeException(tableVersionState.getDescription() + "请稍后~~~");
            }
        }
        return null;
    }

    private boolean getUserLock() {
        if (distributedLock.acquireLock(SCHEDULING_USER_LOCK_KEY, hostHolder.getUser().getUsername())) {
            return true;
        } else if (Objects.equals(redisTemplate.opsForValue().get(SCHEDULING_USER_LOCK_KEY), hostHolder.getUser().getUsername())) {
            return true;
        }
        throw new BeneWakeException(redisTemplate.opsForValue().get(SCHEDULING_USER_LOCK_KEY) + "正在使用了！");
    }

}
