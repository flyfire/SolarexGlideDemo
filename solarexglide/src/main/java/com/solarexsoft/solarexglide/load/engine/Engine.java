package com.solarexsoft.solarexglide.load.engine;

import android.util.Log;

import com.solarexsoft.solarexglide.GlideContext;
import com.solarexsoft.solarexglide.cache.ActiveResources;
import com.solarexsoft.solarexglide.cache.BitmapPool;
import com.solarexsoft.solarexglide.cache.DiskCache;
import com.solarexsoft.solarexglide.cache.Key;
import com.solarexsoft.solarexglide.cache.MemoryCache;
import com.solarexsoft.solarexglide.cache.Resource;
import com.solarexsoft.solarexglide.request.ResourceCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 22:19/2020-01-26
 *    Desc:
 * </pre>
 */

public class Engine implements MemoryCache.ResourceRemoveListener, Resource.ResourceReleaseListener, EngineJob.EngineJobListener {
    private static final String TAG = "Engine";

    public static class LoadStatus {
        private final EngineJob engineJob;
        private final ResourceCallback resourceCallback;

        public LoadStatus(EngineJob engineJob, ResourceCallback resourceCallback) {
            this.engineJob = engineJob;
            this.resourceCallback = resourceCallback;
        }

        public void cancel() {
            engineJob.removeCallback(resourceCallback);
        }
    }

    private final DiskCache diskCache;
    private final BitmapPool bitmapPool;
    private final MemoryCache memoryCache;
    private final ThreadPoolExecutor threadPoolExecutor;

    ActiveResources activeResources;
    Map<Key, EngineJob> jobMap = new HashMap<>();

    public Engine(DiskCache diskCache, BitmapPool bitmapPool, MemoryCache memoryCache, ThreadPoolExecutor threadPoolExecutor) {
        this.diskCache = diskCache;
        this.bitmapPool = bitmapPool;
        this.memoryCache = memoryCache;
        this.threadPoolExecutor = threadPoolExecutor;
        activeResources = new ActiveResources(this);
    }

    public void shutdown() {
        long shutdownSeconds = 5;
        threadPoolExecutor.shutdown();

        try {
            if (!threadPoolExecutor.awaitTermination(shutdownSeconds, TimeUnit.SECONDS)) {
                threadPoolExecutor.shutdownNow();
                if (!threadPoolExecutor.awaitTermination(shutdownSeconds, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Failed to shutdown");
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        diskCache.clear();
        activeResources.shutdownCleanupThread();
    }

    public LoadStatus load(GlideContext glideContext, Object model, int width, int height, ResourceCallback resourceCallback) {
        EngineKey engineKey = new EngineKey(model, width, height);
        Resource resource = activeResources.get(engineKey);
        if (resource != null) {
            Log.d(TAG, "使用活跃缓存数据:" + resource);
            resource.acquire();
            resourceCallback.onResourceReady(resource);
            return null;
        }

        resource = memoryCache.removeVoluntarily(engineKey);
        if (resource != null) {
            Log.d(TAG, "使用内存缓存数据");
            activeResources.activate(engineKey, resource);
            resource.acquire();
            resource.setResourceReleaseListener(engineKey, this);
            resourceCallback.onResourceReady(resource);
            return null;
        }

        EngineJob engineJob = jobMap.get(engineKey);
        if (engineJob != null) {
            Log.d(TAG, "数据正在加载，添加数据加载状态监听");
            engineJob.addCallback(resourceCallback);
            return new LoadStatus(engineJob, resourceCallback);
        }

        engineJob = new EngineJob(threadPoolExecutor, this, engineKey);
        engineJob.addCallback(resourceCallback);

        DecodeJob decodeJob = new DecodeJob(width, height, engineJob, model, diskCache, glideContext);
        engineJob.start(decodeJob);
        jobMap.put(engineKey, engineJob);
        return new LoadStatus(engineJob, resourceCallback);
    }

    @Override
    public void onResourceRemoved(Resource resource) {
        Log.d(TAG, "内存缓存移除，加入bitmap复用池");
        bitmapPool.put(resource.getBitmap());
    }

    @Override
    public void onResourceReleased(Key key, Resource resource) {
        Log.d(TAG, "引用计数为0，移除WeakReference活动缓存，加入LRU内存缓存");
        activeResources.deactivate(key);
        memoryCache.put(key, resource);
    }

    @Override
    public void onEngineJobComplete(EngineJob engineJob, Key key, Resource resource) {
        if (resource != null) {
            resource.setResourceReleaseListener(key, this);
            activeResources.activate(key, resource);
        }
        jobMap.remove(key);
    }

    @Override
    public void onEngineJobCancelled(EngineJob engineJob, Key key) {
        jobMap.remove(key);
    }
}
