package com.benewake.system.controller.task;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

import static com.benewake.system.redis.FeiShuKey.TENANT_ACCESS_TOKEN;

@Slf4j
@Component
public class FeiShuTokenTask {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${feishu.app_id}")
    private String appId;

    @Value("${feishu.app_secret}")
    private String appSecret;

    private final String TENANT_ACCESS_TOKEN_URL = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";

    @Scheduled(cron = "0 0 * * * ?")
    public void cleanTasks() {
        HashMap<String, String> json = new HashMap<>();
        json.put("app_id", appId);
        json.put("app_secret", appSecret);
        String result = HttpRequest.post(TENANT_ACCESS_TOKEN_URL)
                .contentType("application/json;charset=UTF-8"
                ).body(JSON.toJSONString(json))
                .execute()
                .body();
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.getIntValue("code") != 0) {
            log.error("获取token失败{}", result);
        }
        redisTemplate.opsForValue().set(TENANT_ACCESS_TOKEN, jsonObject.getString("tenant_access_token"));
        log.info("获取tenant_access_token{}", LocalDateTime.now());
    }


}
