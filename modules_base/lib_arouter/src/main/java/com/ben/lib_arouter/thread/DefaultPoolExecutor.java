package com.ben.lib_arouter.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.Manifest;

/**
 * 线程池
 *
 * @author: BD
 */
public class DefaultPoolExecutor {
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        private AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "DNRouter # " + mCount.getAndIncrement());
        }
    };

    // cpu核心数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // 核心线程和最大线程都是cpu核心数+1
    private static final int MAX_CORE_POOL_SIZE = CPU_COUNT + 1;
    // 存活30s，回收线程
    private static final long SURPLUS_THREAD_LIFE = 30L;

    public static ThreadPoolExecutor newDefaultPoolExecutor(int corPoolSize) {
        if (corPoolSize == 0) {
            return null;
        }
        corPoolSize = Math.min(corPoolSize, MAX_CORE_POOL_SIZE);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corPoolSize, corPoolSize, SURPLUS_THREAD_LIFE, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(64), sThreadFactory);
        // 核心线程也会被销毁
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
}
