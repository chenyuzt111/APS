package com.benewake.system.service.feishu.Impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.benewake.system.entity.feishu.Message;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.service.feishu.FeiShuService;
import com.benewake.system.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.benewake.system.redis.FeiShuKey.TENANT_ACCESS_TOKEN;

@Slf4j
@Component
public class FeiShuServiceImpl implements FeiShuService {

    private final String SEND_MESSAGE = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=email";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${feishu.scheduling_template_id}")
    private String templateId;

    @Autowired
    private HostHolder hostHolder;

    public void sendMessage(String msg) {
        SysUser user = hostHolder.getUser();
        Message message = new Message();
        String email = user.getEmail();
        message.setReceiveId(email);
        message.setMsgType("interactive");
        String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        message.setContent("{\"type\": \"template\", \"data\": { \"template_id\": \"" + templateId + "\", \"template_variable\": {\"user_at\": \"<at email=\\\"" + email + "\\\"></at>\", \"res\": \"" + msg + "\", \"date\": \"" + formattedDateTime + "\"} }}");
        String body = JSON.toJSONString(message);
        System.out.println(body);
        String token = redisTemplate.opsForValue().get(TENANT_ACCESS_TOKEN);
        if (StringUtils.isNotEmpty(token)) {
            String result = HttpRequest.post(SEND_MESSAGE)
                    .contentType("application/json;charset=UTF-8")
                    .header("Authorization", "Bearer " + token)
                    .body(body)
                    .execute().body();
            log.info(result);
            return;
        }
        log.error("发送飞书消息获取Token为空");
    }
}
