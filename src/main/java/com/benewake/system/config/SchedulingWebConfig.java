package com.benewake.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulingWebConfig implements ApplicationContextAware {
//
//    private final List<CleanTaskAble> taskList = new ArrayList<>(8);
//
//    private final ThreadPoolExecutor exec;
//
//    public SchedulingWebConfig() {
//        /*
//         * corePoolSize为0，没有及时性要求，设置为0减少内存消耗。
//         * maximumPoolSize为4，不需要占用太多的资源；
//         * keepAliveTime为60S，意味着线程空闲时间超过60S就会被杀死；
//         * LinkedBlockingQueue，可变长度任务队列，最大值为100。
//         */
//        this.exec = new ThreadPoolExecutor(0, 4,
//                60L, TimeUnit.SECONDS,
//                new LinkedBlockingQueue<Runnable>(100));
//    }




    @Override
    public void setApplicationContext(ApplicationContext applicationContext)  {
//        taskList.addAll(applicationContext.getBeansOfType(CleanTaskAble.class).values());
    }
}

