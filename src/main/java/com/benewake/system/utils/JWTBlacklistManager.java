package com.benewake.system.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Component
public class JWTBlacklistManager {
    private final String TOKEN_BLACKLIST_KEY = "token::blacklist";

    private final String BLACKLISTED_VALUE = "1";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token: " + e.getMessage());
        }
    }

    //添加黑名信息
    public void addToBlacklist(String token) {
        redisTemplate.opsForValue().set(hashToken(TOKEN_BLACKLIST_KEY + token), BLACKLISTED_VALUE, 7, TimeUnit.DAYS);
    }

    //查询是否在黑名单
    public boolean isBlacklisted(String token) {
        String isBlacklisted = redisTemplate.opsForValue().get(hashToken(TOKEN_BLACKLIST_KEY + token));
        return isBlacklisted != null && isBlacklisted.equals(BLACKLISTED_VALUE);
    }
}
