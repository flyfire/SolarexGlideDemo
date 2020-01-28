package com.solarexsoft.solarexglide.facade;

import android.app.ActivityManager;
import android.content.Context;
import android.util.DisplayMetrics;

import com.solarexsoft.solarexglide.cache.ArrayPool;
import com.solarexsoft.solarexglide.cache.BitmapPool;
import com.solarexsoft.solarexglide.cache.DiskCache;
import com.solarexsoft.solarexglide.cache.DiskLruCacheWrapper;
import com.solarexsoft.solarexglide.cache.LruArrayPool;
import com.solarexsoft.solarexglide.cache.LruBitmapPool;
import com.solarexsoft.solarexglide.cache.LruMemoryCache;
import com.solarexsoft.solarexglide.cache.MemoryCache;
import com.solarexsoft.solarexglide.load.engine.Engine;
import com.solarexsoft.solarexglide.load.engine.GlideExecutor;
import com.solarexsoft.solarexglide.request.RequestOptions;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 16:13/2020-01-28
 *    Desc:
 * </pre>
 */

public class GlideBuilder {
    MemoryCache memoryCache;
    DiskCache diskCache;
    BitmapPool bitmapPool;
    ArrayPool arrayPool;
    RequestOptions defaultRequestOptions = new RequestOptions();
    ThreadPoolExecutor executor;
    Engine engine;

    public Glide build(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        int maxSize = getMaxSize(activityManager);
        if (arrayPool == null) {
            arrayPool = new LruArrayPool();
        }
        int availableSize = maxSize - arrayPool.getMaxSize();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        // 一个屏幕大小的ARGB所占的内存大小
        int screenSize = widthPixels * heightPixels * 4;

        float bitmapPoolSize = screenSize * 4.0f;
        float memoryCacheSize = screenSize * 2.0f;
        if (bitmapPoolSize + memoryCacheSize <= availableSize) {
            bitmapPoolSize = Math.round(bitmapPoolSize);
            memoryCacheSize = Math.round(memoryCacheSize);
        } else {
            float singlePart = availableSize / 6.0f;
            bitmapPoolSize = Math.round(singlePart * 4);
            memoryCacheSize = Math.round(singlePart * 2);
        }
        if (bitmapPool == null) {
            bitmapPool = new LruBitmapPool((int)bitmapPoolSize);
        }
        if (memoryCache == null) {
            memoryCache = new LruMemoryCache((int)memoryCacheSize);
        }
        if (diskCache == null) {
            diskCache = new DiskLruCacheWrapper(context);
        }
        if (executor == null) {
            executor = GlideExecutor.newExecutor();
        }
        engine = new Engine(diskCache, bitmapPool, memoryCache, executor);
        memoryCache.setResourceRemoveListener(engine);
        return new Glide(context, this);
    }

    private int getMaxSize(ActivityManager activityManager) {
        int memoryBytes = activityManager.getMemoryClass() * 1024 * 1024;
        return Math.round(memoryBytes * 0.4f);
    }

    public void setMemoryCache(MemoryCache memoryCache) {
        this.memoryCache = memoryCache;
    }

    public void setDiskCache(DiskCache diskCache) {
        this.diskCache = diskCache;
    }

    public void setBitmapPool(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    public void setArrayPool(ArrayPool arrayPool) {
        this.arrayPool = arrayPool;
    }

    public void setDefaultRequestOptions(RequestOptions defaultRequestOptions) {
        this.defaultRequestOptions = defaultRequestOptions;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
