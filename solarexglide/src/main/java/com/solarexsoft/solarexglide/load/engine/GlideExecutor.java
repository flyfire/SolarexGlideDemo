package com.solarexsoft.solarexglide.load.engine;

import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 22:49/2020-01-26
 *    Desc:
 * </pre>
 */

public class GlideExecutor {
    private static int bestThreadCount;

    public static int calculateBestThreadCount() {
        if (bestThreadCount == 0) {
            bestThreadCount = Math.min(4, Runtime.getRuntime().availableProcessors());
        }
        return bestThreadCount;
    }

    private static final class DefaultThreadFactory implements ThreadFactory {
        AtomicInteger atomicInteger = new AtomicInteger();
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r, "solarex-glide-" + atomicInteger.getAndIncrement());
            return thread;
        }
    }

    public static ThreadPoolExecutor newExecutor() {
        int threadCount = calculateBestThreadCount();
        return new ThreadPoolExecutor(
                threadCount,
                threadCount,
                0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory());
    }
}
