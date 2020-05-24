package com.example.hopskip;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ThreadHandler {
    private final static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    public static void addRunnable(Runnable runnable) {
        executor.setKeepAliveTime(5, TimeUnit.SECONDS);
        executor.execute(runnable);
    }

    public static void shutdown() {
        executor.shutdown();
        executor.shutdownNow();
    }

    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
