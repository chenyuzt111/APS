package com.benewake.system.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class DistributedLock {

    //排程用户锁 锁时间
    //表示所得过期时间，如果一个进程获取了锁并在指定的时间内没有释放，锁将自动释放
    private static final int LOCK_EXPIRE_TIME_MINUTES = 6; // 锁的超时时间，单位分钟

    //这是一个用于和redis服务器交互的spring"RedisTemplate"，被配置为<string，string>
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //acquireLock有两个重载

    //第一个使用LOCK_EXPIRE_TIME_MINUTES作为锁的超时时间
    public boolean acquireLock(String lockKey, String requestId) {
        // 使用SET命令尝试获取锁
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, LOCK_EXPIRE_TIME_MINUTES, TimeUnit.MINUTES);
        return success != null && success;
    }

    //第二次允许调用者每次获取锁的时候指定超时时间
    public boolean acquireLock(String lockKey, String requestId ,Integer timeout ,TimeUnit unit) {
        // 使用SET命令尝试获取锁
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, timeout, unit);
        return success != null && success;
    }

    public boolean releaseLock(String lockKey, String requestId) {
        // 使用Lua脚本释放锁，确保原子性
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class)
                , Collections.singletonList(lockKey), requestId);
        return result != null && (long) result == 1;
    }

    public void startLockRenewalTask(String lockKey) {
            // 续期锁的过期时间
        redisTemplate.expire(lockKey, LOCK_EXPIRE_TIME_MINUTES, TimeUnit.MINUTES);
    }
}
