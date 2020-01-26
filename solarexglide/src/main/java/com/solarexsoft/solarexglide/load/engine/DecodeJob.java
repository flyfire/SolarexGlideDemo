package com.solarexsoft.solarexglide.load.engine;

import android.util.Log;

import com.solarexsoft.solarexglide.GlideContext;
import com.solarexsoft.solarexglide.cache.DiskCache;
import com.solarexsoft.solarexglide.cache.Key;
import com.solarexsoft.solarexglide.cache.Resource;
import com.solarexsoft.solarexglide.load.generator.DataCacheGenerator;
import com.solarexsoft.solarexglide.load.generator.DataGenerator;
import com.solarexsoft.solarexglide.load.generator.SourceGenerator;

import java.io.IOException;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:48/2020-01-26
 *    Desc:
 * </pre>
 */

public class DecodeJob implements Runnable, DataGenerator.DataGeneratorCallback {
    private static final String TAG = "DecodeJob";

    private final int width;
    private final int height;
    private final DecodeCallback callback;
    private final Object model;
    private final DiskCache diskCache;
    private final GlideContext glideContext;
    private DataGenerator currentGenerator;
    private boolean isCancelled;
    private boolean isCallbackNotified;
    private Stage stage;
    private Key sourceKey;

    public DecodeJob(int width, int height, DecodeCallback callback, Object model, DiskCache diskCache, GlideContext glideContext) {
        this.width = width;
        this.height = height;
        this.callback = callback;
        this.model = model;
        this.diskCache = diskCache;
        this.glideContext = glideContext;
    }

    interface DecodeCallback {
        void onResourceReady(Resource resource);

        void onResourceLoadFailed(Throwable throwable);
    }

    private enum Stage {
        INITIALIZE,
        DATA_CACHE,
        SOURCE,
        FINISHED;
    }

    public void cancel() {
        isCancelled = true;
        if (currentGenerator != null) {
            currentGenerator.cancel();
        }
    }
    @Override
    public void run() {
        try {
            Log.d(TAG, "开始加载数据");
            if (isCancelled) {
                Log.d(TAG, "取消加载数据");
                callback.onResourceLoadFailed(new IOException("Canceled"));
                return;
            }
            // todo
            stage = getNextStage(Stage.INITIALIZE);
            currentGenerator = getNextGenerator();
            runGenerators();
        } catch (Throwable t) {
            callback.onResourceLoadFailed(t);
        }
    }

    private DataGenerator getNextGenerator() {
        switch (stage) {
            case DATA_CACHE:
                Log.d(TAG, "使用磁盘缓存加载器");
                return new DataCacheGenerator(model, this, glideContext, diskCache);
            case SOURCE:
                Log.d(TAG, "使用源资源加载器");
                return new SourceGenerator(this, glideContext, model);
            case FINISHED:
                return null;
            default:
                throw new IllegalStateException("Unrecognized stage: " + stage);
        }
    }

    private void runGenerators() {
        boolean isStarted = false;
        while (!isCancelled && currentGenerator != null && !isStarted) {
            isStarted = currentGenerator.startNext();
            if (isStarted) {
                break;
            }
            stage = getNextStage(stage);
            if (stage == Stage.FINISHED) {
                Log.d(TAG, "状态结束,没有加载器能够加载对应数据");
                break;
            }
            currentGenerator = getNextGenerator();
        }

        if ((stage == Stage.FINISHED || isCancelled) && !isStarted){
            notifyFailed();
        }
    }

    private void notifyFailed() {
        Log.d(TAG, "加载失败");
        if (!isCallbackNotified) {
            isCallbackNotified = true;
            callback.onResourceLoadFailed(new RuntimeException("Failed to load resource"));
        }
    }

    private Stage getNextStage(Stage current) {
        switch (current) {
            case INITIALIZE:
                return Stage.DATA_CACHE;
            case DATA_CACHE:
                return Stage.SOURCE;
            case SOURCE:
            case FINISHED:
                return Stage.FINISHED;
            default:
                throw new IllegalArgumentException("Unrecognized stage: " + current);

        }
    }

    @Override
    public void onDataFetcherReady(Key sourceKey, Object data, DataSource dataSource) {
        this.sourceKey = sourceKey;
        Log.d(TAG, "加载成功，开始解码数据");
        runLoadPath(data, dataSource);
    }

    private <Data> void runLoadPath(Data data, DataSource dataSource) {
        // todo
    }

    @Override
    public void onDataFetcherFailed(Key sourceKey, Exception e) {
        Log.d(TAG, "加载失败，尝试使用下一个加载器: " + e.getMessage());
        runGenerators();
    }
}
