package com.solarexsoft.solarexglide.load.generator;

import android.util.Log;

import com.solarexsoft.solarexglide.facade.GlideContext;
import com.solarexsoft.solarexglide.cache.DiskCache;
import com.solarexsoft.solarexglide.cache.Key;
import com.solarexsoft.solarexglide.load.modelloader.DataFetcher;
import com.solarexsoft.solarexglide.load.modelloader.ModelLoader;

import java.io.File;
import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 20:06/2020-01-26
 *    Desc:
 * </pre>
 */

public class DataCacheGenerator implements DataGenerator, DataFetcher.DataFetcherCallback {

    private static final String TAG = "DataCacheGenerator";

    private final DataGeneratorCallback dataGeneratorCallback;
    private final GlideContext glideContext;
    private final DiskCache diskCache;

    private List<ModelLoader<File, ?>> modelLoaders;
    private List<Key> keys;
    private int sourceIdIndex = -1;
    private Key sourceKey;
    private int modelLoaderIndex;
    private File cacheFile;
    private ModelLoader.LoadData<?> loadData;

    public DataCacheGenerator(Object model,DataGeneratorCallback dataGeneratorCallback, GlideContext glideContext, DiskCache diskCache) {
        this.dataGeneratorCallback = dataGeneratorCallback;
        this.glideContext = glideContext;
        this.diskCache = diskCache;

        keys = glideContext.getRegistry().getKeys(model);
    }

    @Override
    public boolean startNext() {
        Log.d(TAG, "磁盘加载器开始加载");
        while (modelLoaders == null) {
            sourceIdIndex++;
            if (sourceIdIndex >= keys.size()) {
                return false;
            }
            Key sourceIdKey = keys.get(sourceIdIndex);
            cacheFile = diskCache.get(sourceIdKey);
            Log.d(TAG, "磁盘缓存位于: " + cacheFile);
            if (cacheFile != null) {
                sourceKey = sourceIdKey;
                Log.d(TAG, "获得所有文件加载器");
                modelLoaders = glideContext.getRegistry().getModelLoaders(cacheFile);
                modelLoaderIndex = 0;
            }
        }
        boolean started = false;
        while (!started && hasNextModelLoader()) {
            ModelLoader<File, ?> modelLoader = modelLoaders.get(modelLoaderIndex++);
            loadData = modelLoader.buildData(cacheFile);
            Log.d(TAG, "获得加载设置数据");
            if (loadData != null && glideContext.getRegistry().hasLoadPath(loadData.fetcher.getDataClass())) {
                Log.d(TAG, "加载设置数据输出数据对应能够查找有效的解码器路径，开始加载数据");
                started = true;
                loadData.fetcher.loadData(this);
            }
        }
        return started;
    }

    private boolean hasNextModelLoader() {
        return modelLoaderIndex < modelLoaders.size();
    }

    @Override
    public void cancel() {
        if (loadData != null) {
            loadData.fetcher.cancel();
        }
    }

    @Override
    public void onFetcherReady(Object data) {
        Log.d(TAG, "加载器加载数据成功回调");
        dataGeneratorCallback.onDataFetcherReady(sourceKey, data, DataGeneratorCallback.DataSource.CACHE);
    }

    @Override
    public void onFetcherFailed(Exception ex) {
        Log.d(TAG, "加载器加载数据失败回调");
        dataGeneratorCallback.onDataFetcherFailed(sourceKey, ex);
    }
}
