package com.ameat.application;

import java.util.concurrent.*;

public class EngineFactory {
    private static ExecutorService threadPools = null;
    public static synchronized ExecutorService createThreadPool() {
        if (threadPools == null) {
            threadPools = new ThreadPoolExecutor(1000, 1000,
                    240L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }
        return threadPools;
    }

    public static synchronized ExecutorService fixedThreadPool(int number) {
        if (threadPools == null) {
            threadPools = Executors.newFixedThreadPool(number);
        }
        return threadPools;
    }
}
