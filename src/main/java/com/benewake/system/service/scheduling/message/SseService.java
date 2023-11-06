package com.benewake.system.service.scheduling.message;

import com.benewake.system.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.*;

@Slf4j
@Service
public class SseService {

    private volatile SseEmitter sseEmitter;

    public SseEmitter connect() {
        sseEmitter = new SseEmitter(200000L);
        sseEmitter.onCompletion(() -> {
            sseEmitter = null;
        });

        System.out.println("sse建立 ------------------------" + sseEmitter);
        Runnable runnable = () -> {
            try {
                while (true) {
                    if (sseEmitter != null) {
                        sseEmitter.send("1");
//                        System.out.println(Thread.currentThread().getName() + "sse---心跳" + sseEmitter);
                        Thread.sleep(10000);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };

        new Thread(runnable).start();
        return sseEmitter;
    }



    public void sendMessage(String username, String message) {
        try {
            sseEmitter.send(message);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("username" + username + "排程发送消息失败" + message);
        }
    }
}