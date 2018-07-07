package com.solarexsoft.solarexglide.load.model.interfaces;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 22:22/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class ModelLoaderRegistry {
    private final List<Entry<?, ?>> mEntries = new ArrayList<>();

    public synchronized <Model, Data> void add(
            Class<Model> modelClass,
            Class<Data> dataClass,
            ModelLoader.ModelLoaderFactory<? extends Model, ? extends Data> factory) {
        Entry<Model, Data> entry = new Entry<>(modelClass, dataClass, factory);
        mEntries.add(entry);
    }

    public synchronized <Model, Data> ModelLoader<Model, Data> build(Class<Model> modelClass,
                                                                     Class<Data> dataClass) {
        List<ModelLoader<Model, Data>> loaders = new ArrayList<>();
        for (Entry<?, ?> entry : mEntries) {
            if (entry.handles(modelClass, dataClass)) {
                loaders.add((ModelLoader<Model, Data>) entry.mFactory.build(this));
            }
        }
        if (loaders.size() > 1) {
            return new MultiModelLoader<>(loaders);
        } else if (loaders.size() == 1) {
            return loaders.get(0);
        }

        throw new RuntimeException("Cant find Model: " + modelClass.getName() + " matches Data: "
                + dataClass.getName());
    }

    public <Model> List<ModelLoader<Model, ?>> getModelLoaders(Class<Model> modelClass) {
        List<ModelLoader<Model, ?>> modelLoaders = new ArrayList<>();
        for (Entry<?, ?> entry : mEntries) {
            if (entry.handles(modelClass)) {
                modelLoaders.add((ModelLoader<Model, ?>) entry.mFactory.build(this));
            }
        }
        return modelLoaders;
    }

    private static class Entry<Model, Data> {
        private final Class<Model> mModelClass;
        private final Class<Data> mDataClass;
        private final ModelLoader.ModelLoaderFactory<? extends Model, ? extends Data> mFactory;

        public Entry(Class<Model> modelClass, Class<Data> dataClass, ModelLoader
                .ModelLoaderFactory<? extends Model, ? extends Data> factory) {
            mModelClass = modelClass;
            mDataClass = dataClass;
            mFactory = factory;
        }

        public boolean handles(@NonNull Class<?> modelClass, @NonNull Class<?> dataClass) {
            // A.isAssignableFrom(B)
            // A a = new B(); 是否成立
            return this.mModelClass.isAssignableFrom(modelClass) && this.mDataClass
                    .isAssignableFrom(dataClass);
        }

        public boolean handles(@NonNull Class<?> modelClass) {
            return this.mModelClass.isAssignableFrom(modelClass);
        }
    }
}
