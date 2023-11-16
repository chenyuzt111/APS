package com.benewake.system.service.scheduling.message;

import com.benewake.system.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.*;

@Slf4j
@Service
public class SseService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile Future<?> heartbeatTask;
    private volatile SseEmitter sseEmitter;

    public SseEmitter connect() {
        if (sseEmitter != null) {
            // 如果已经有连接，取消原来的任务和SseEmitter
            sseEmitter.complete();
            heartbeatTask.cancel(true);
        }

        sseEmitter = new SseEmitter(200000L);
        sseEmitter.onCompletion(() -> {
            sseEmitter = null;
        });

        System.out.println("sse建立 ------------------------" + sseEmitter);

        heartbeatTask = executor.submit(() -> {
            while (true) {
                try {
                    if (sseEmitter != null) {
                        sseEmitter.send("1");
                        System.out.println(Thread.currentThread().getName() + "sse");
                        Thread.sleep(10000);
                    } else {
                        break;
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });

        return sseEmitter;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    public void sendMessage(String username, String message) {
        try {
            sseEmitter.send(message);
            sseEmitter.complete();
            heartbeatTask.cancel(true);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("username" + username + "排程发送消息失败" + message);
        }
    }
}