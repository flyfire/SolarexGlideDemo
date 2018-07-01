package com.solarexsoft.solarexglide.load.model.interfaces;

import com.solarexsoft.solarexglide.cache.interfaces.Key;
import com.solarexsoft.solarexglide.load.data.impl.MultiFetcher;
import com.solarexsoft.solarexglide.load.data.interfaces.DataFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 22:51/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class MultiModelLoader<Model, Data> implements ModelLoader<Model, Data> {
    private final List<ModelLoader<Model, Data>> mModelLoaders;

    public MultiModelLoader(List<ModelLoader<Model, Data>> modelLoaders) {
        mModelLoaders = modelLoaders;
    }

    @Override
    public LoadData<Data> buildLoadData(Model model) {
        Key sourceKey = null;
        int size = mModelLoaders.size();
        List<DataFetcher<Data>> fetchers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ModelLoader<Model, Data> modelLoader = mModelLoaders.get(i);
            LoadData<Data> loadData = modelLoader.buildLoadData(model);
            if (loadData != null) {
                sourceKey = loadData.mKey;
                fetchers.add(loadData.mDataFetcher);
            }
        }
        return !fetchers.isEmpty() && sourceKey != null ? new LoadData<Data>(sourceKey, new
                MultiFetcher<Data>(fetchers)) : null;
    }

    @Override
    public boolean handles(Model model) {
        for (ModelLoader<Model, Data> modelLoader : mModelLoaders) {
            if (modelLoader.handles(model)) {
                return true;
            }
        }
        return false;
    }
}
