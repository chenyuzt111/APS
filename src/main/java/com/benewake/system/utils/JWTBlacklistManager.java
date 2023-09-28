package com.benewake.system.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JWTBlacklistManager {
    private final String TOKEN_BLACKLIST_KEY = "token::blacklist";

    private final String BLACKLISTED_VALUE = "1";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //添加黑名单信息
    public void addToBlacklist(String token) {
        redisTemplate.opsForValue().set(TOKEN_BLACKLIST_KEY + token, BLACKLISTED_VALUE, 7, TimeUnit.DAYS);
    }

    //查询是否在黑名单
    public boolean isBlacklisted(String token) {
        String isBlacklisted = redisTemplate.opsForValue().get(TOKEN_BLACKLIST_KEY + token);
        return isBlacklisted != null && isBlacklisted.equals(BLACKLISTED_VALUE);
    }
}
