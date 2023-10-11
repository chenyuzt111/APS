package com.benewake.system.service.message;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public SseEmitter connect(String userId) {
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitterMap.put(userId, sseEmitter);
        sseEmitter.onCompletion(() -> disconnect(userId)); // 添加断开连接时的处理
        return sseEmitter;
    }

    public void disconnect(String userId) {
        sseEmitterMap.remove(userId); // 断开连接时移除SseEmitter
    }

    /**
     * 给指定用户发送消息
     */
    public void sendMessage(String userId, String message) {
        SseEmitter sseEmitter = sseEmitterMap.get(userId);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().data(message)); // 使用SseEmitter的event()方法创建消息事件
            } catch (IOException e) {
                sseEmitterMap.remove(userId);
                e.printStackTrace();
            }
        }
    }
}
