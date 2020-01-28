package com.solarexsoft.solarexglide.load.generator;

import android.util.Log;

import com.solarexsoft.solarexglide.facade.GlideContext;
import com.solarexsoft.solarexglide.load.modelloader.DataFetcher;
import com.solarexsoft.solarexglide.load.modelloader.ModelLoader;

import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 20:24/2020-01-26
 *    Desc:
 * </pre>
 */

public class SourceGenerator implements DataGenerator, DataFetcher.DataFetcherCallback<Object> {

    private static final String TAG = "SourceGenerator";

    private final DataGeneratorCallback dataGeneratorCallback;
    private final GlideContext glideContext;

    private int loadDataListIndex;
    private List<ModelLoader.LoadData<?>> loadDataList;
    private ModelLoader.LoadData<?> loadData;

    public SourceGenerator(DataGeneratorCallback dataGeneratorCallback, GlideContext glideContext, Object model) {
        this.dataGeneratorCallback = dataGeneratorCallback;
        this.glideContext = glideContext;

        loadDataList = glideContext.getRegistry().getLoadDatas(model);
    }

    @Override
    public boolean startNext() {
        boolean started = false;
        while (!started && hasNextModelLoader()) {
            loadData = loadDataList.get(loadDataListIndex++);
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
        return loadDataListIndex < loadDataList.size();
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
        dataGeneratorCallback.onDataFetcherReady(loadData.key, data, DataGeneratorCallback.DataSource.REMOTE);
    }

    @Override
    public void onFetcherFailed(Exception ex) {
        Log.d(TAG, "加载器加载数据失败回调");
        dataGeneratorCallback.onDataFetcherFailed(loadData.key, ex);
    }
}
