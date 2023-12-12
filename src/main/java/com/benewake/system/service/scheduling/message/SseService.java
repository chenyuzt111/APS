package com.benewake.system.service.scheduling.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
public class SseService {

//    创建一个单线程的ExecutorService，用于执行后台任务
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
//    声明一个future对象，用于对持有心跳任务的任用
    private volatile Future<?> heartbeatTask;
//    声明一个SseEmitter对象，用于持有SSE连接
    private volatile SseEmitter sseEmitter;

//     创建方法建立SSE连接
    public SseEmitter connect() {
        if (sseEmitter != null) {
            // 如果已经有连接，取消原来的任务和SseEmitter
            sseEmitter.complete();
            heartbeatTask.cancel(true);
        }
//      创建一个新的SseEmitter对象，设置超时时间为200000毫秒
        sseEmitter = new SseEmitter(200000L);
//        当sse连接完成时的回调将sseEmitter设置为null
        sseEmitter.onCompletion(() -> {
            sseEmitter = null;
        });

        log.info("sse建立 ------------------------" + sseEmitter);
//      提交一个新的任务，用于定期发送心跳信息，保持SSE连接的活跃状态
        heartbeatTask = executor.submit(() -> {
//            在心跳任务中，通过无线循环，定期发送心跳信息
            while (true) {
                try {
                    if (sseEmitter != null) {
                        sseEmitter.send("1");
                        log.info(Thread.currentThread().getName() + "SSE");
                        Thread.sleep(10000);
                    } else {
                        break;
                    }
                } catch (IOException | InterruptedException e) {
                    log.warn(Thread.currentThread().getName() + "SSE断开" + e.getMessage());
                    break;
                }
            }
        });

//        返回建立的sseEmitter对象
        return sseEmitter;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    public void sendMessage(String username, String message) {
        try {
            if (sseEmitter != null) {
                sseEmitter.send(message);
                sseEmitter.complete();
            }
            if (heartbeatTask != null) {
                heartbeatTask.cancel(true);
            }
        } catch (Exception e) {
            log.error("username：" + username + "排程发送消息失败 --message：" + message + "----原因：" + e.getMessage());
        }
    }
}