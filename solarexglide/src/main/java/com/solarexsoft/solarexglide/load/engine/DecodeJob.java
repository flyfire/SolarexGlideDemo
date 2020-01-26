package com.solarexsoft.solarexglide.load.engine;

import android.util.Log;

import com.solarexsoft.solarexglide.GlideContext;
import com.solarexsoft.solarexglide.cache.DiskCache;
import com.solarexsoft.solarexglide.cache.Key;
import com.solarexsoft.solarexglide.cache.Resource;
import com.solarexsoft.solarexglide.load.generator.DataGenerator;

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
        } catch (Throwable t) {
            callback.onResourceLoadFailed(t);
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

    }

    @Override
    public void onDataFetcherFailed(Key sourceKey, Exception e) {

    }
}
