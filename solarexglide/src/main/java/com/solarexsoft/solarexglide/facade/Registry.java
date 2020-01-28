package com.solarexsoft.solarexglide.facade;

import com.solarexsoft.solarexglide.cache.Key;
import com.solarexsoft.solarexglide.load.codec.ResourceDecoder;
import com.solarexsoft.solarexglide.load.codec.ResourceDecoderRegistry;
import com.solarexsoft.solarexglide.load.engine.LoadPath;
import com.solarexsoft.solarexglide.load.modelloader.ModelLoader;
import com.solarexsoft.solarexglide.load.modelloader.ModelLoaderRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 16:33/2020-01-28
 *    Desc:
 * </pre>
 */

public class Registry {
    private final ModelLoaderRegistry modelLoaderRegistry = new ModelLoaderRegistry();
    private final ResourceDecoderRegistry resourceDecoderRegistry = new ResourceDecoderRegistry();

    public <Model, Data> Registry add(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory) {
        modelLoaderRegistry.add(modelClass, dataClass, factory);
        return this;
    }

    public <T> Registry register(Class<T> dataClass, ResourceDecoder<T> decoder) {
        resourceDecoderRegistry.add(dataClass, decoder);
        return this;
    }

    public boolean hasLoadPath(Class<?> dataClass) {
        return getLoadPath(dataClass) != null;
    }

    public <Data> LoadPath<Data> getLoadPath(Class<Data> dataClass) {
        List<ResourceDecoder<Data>> decoders = resourceDecoderRegistry.getDecoders(dataClass);
        if (decoders != null && decoders.size() > 0) {
            return new LoadPath<>(dataClass, decoders);
        } else {
            return null;
        }
    }

    public <Model> List<ModelLoader<Model, ?>> getModelLoaders(Model model) {
        Class<Model> modelClass = (Class<Model>) model.getClass();
        List<ModelLoader<Model, ?>> modelLoaders = modelLoaderRegistry.getModelLoaders(modelClass);
        return modelLoaders;
    }

    public List<ModelLoader.LoadData<?>> getLoadDatas(Object model) {
        List<ModelLoader.LoadData<?>> loadData = new ArrayList<>();
        List<ModelLoader<Object, ?>> modelLoaders = getModelLoaders(model);
        for (ModelLoader<Object, ?> modelLoader : modelLoaders) {
            ModelLoader.LoadData<?> current = modelLoader.buildData(model);
            if (current != null) {
                loadData.add(current);
            }
        }
        return loadData;
    }

    public List<Key> getKeys(Object model) {
        List<Key> keys = new ArrayList<>();
        List<ModelLoader.LoadData<?>> loadDatas = getLoadDatas(model);
        for (ModelLoader.LoadData<?> loadData : loadDatas) {
            keys.add(loadData.key);
        }
        return keys;
    }
}
