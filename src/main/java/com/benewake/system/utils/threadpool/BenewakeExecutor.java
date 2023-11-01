package com.benewake.system.utils.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

public class BenewakeExecutor {
    private static final int MIN_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int MAX_SIZE;
    private static final int BLOCK_SIZE;
    private static BenewakeExecutor.BaseThreadPoolExecutor EXECUTOR;

    static {
        MAX_SIZE = MIN_SIZE * 20;
        BLOCK_SIZE = MIN_SIZE * 10;
        EXECUTOR = new BenewakeExecutor.BaseThreadPoolExecutor(MIN_SIZE, MAX_SIZE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(BLOCK_SIZE), new CustomizableThreadFactory("BenewakeExecutor.EXE"));
    }

    public BenewakeExecutor() {
    }

    public static int poolSize() {
        return EXECUTOR.getPoolSize();
    }

    public static int activeCount() {
        return EXECUTOR.getActiveCount();
    }

    public static void execute(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }

    public static void execute(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
        threadPoolExecutor.execute(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return EXECUTOR.submit(callable);
    }

    public static <T> Future<T> submit(Callable<T> callable, ThreadPoolExecutor threadPoolExecutor) {
        return threadPoolExecutor.submit(callable);
    }

    protected static class BaseThreadPoolExecutor extends ThreadPoolExecutor {
        private int poolSize;
        private int activeSize;

        public int getActiveSize() {
            return this.activeSize;
        }

        public int getPoolSize() {
            return this.poolSize;
        }

        public BaseThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        protected void afterExecute(Runnable r, Throwable t) {
            this.poolSize = this.getPoolSize();
            this.activeSize = this.getActiveCount();
        }
    }
}
