package com.benewake.system.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class DistributedLock {

    //    private static final String LOCK_KEY = "mylock"; // 锁的键名
    private static final int LOCK_EXPIRE_TIME_SECONDS = 300000; // 锁的超时时间，单位秒

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean acquireLock(String lockKey, String requestId) {
        // 使用SET命令尝试获取锁
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, LOCK_EXPIRE_TIME_SECONDS, TimeUnit.SECONDS);
        return success != null && success;
    }

    public boolean releaseLock(String lockKey, String requestId) {
        // 使用Lua脚本释放锁，确保原子性
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), Collections.singletonList(lockKey), requestId);
        return result != null && (long) result == 1;
    }
}
