package com.benewake.system.service.message;

import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.threadpool.BenewakeExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @Autowired
    private HostHolder hostHolder;

    public SseEmitter connect() {
        SseEmitter sseEmitter = new SseEmitter(2000000L);
        sseEmitterMap.put(hostHolder.getUser().getUsername(), sseEmitter);
        sseEmitter.onCompletion(() -> disconnect(hostHolder.getUser().getUsername())); // 添加断开连接时的处理
        BenewakeExecutor.execute(() -> {
            try {
                while (true) {
                    sseEmitter.send("");
                    Thread.sleep(15000); // 每45秒发送一次
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

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
