//package com.benewake.system.service.message;
//
//import com.benewake.system.entity.system.SysUser;
//import com.benewake.system.utils.HostHolder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//
//@Service
//public class SseServiceb {
//    private ScheduledExecutorService heartbeatExecutor = Executors.newScheduledThreadPool(1);
////    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
//    private SseEmitter sseEmitter = null;
//    @Autowired
//    private HostHolder hostHolder;
//
//    private volatile boolean isRunning = true;
//
//    public SseEmitter connect() {
//        sseEmitter = new SseEmitter(200000L);
//        SysUser user = hostHolder.getUser();
//        sseEmitter.onCompletion(() -> disconnect(hostHolder.getUser().getUsername())); // 添加断开连接时的处理
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        };
//
//
//        heartbeatExecutor.execute(() -> {
//            while (isRunning) {
//                try {
//                    hostHolder.setUser(user);
//                    System.out.println("sseEmitter:" + sseEmitter + "size:" + "---execute:" + Thread.currentThread().getName() + "user:" + hostHolder.getUser().getUsername());
//                    sseEmitter.send("1");
//                    Thread.sleep(15000); // 每45秒发送一次
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                    break;
//                }
//            }
//        });
//
//        return sseEmitter;
//    }
//
//    public void disconnect(String userId) {
//        sseEmitterMap.remove(userId);
//        isRunning = false; // 设置标志以终止心跳线程
//    }
//
//    /**
//     * 给指定用户发送消息
//     */
//    public void sendMessage(String userId, String message) {
//        SseEmitter sseEmitter = sseEmitterMap.get(userId);
//        if (sseEmitter != null) {
//            try {
//                sseEmitter.send(SseEmitter.event().data(message)); // 使用SseEmitter的event()方法创建消息事件
//            } catch (IOException e) {
//                sseEmitterMap.remove(userId);
//                e.printStackTrace();
//            }
//        }
//    }
//}
