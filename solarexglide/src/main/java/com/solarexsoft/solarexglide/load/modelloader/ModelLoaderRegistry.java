package com.solarexsoft.solarexglide.load.modelloader;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 09:19/2020-01-22
 *    Desc:
 * </pre>
 */

public class ModelLoaderRegistry {
    private List<Entry<?, ?>> entries = new ArrayList<>();

    public synchronized <Model, Data> void add(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory){
        entries.add(new Entry<>(modelClass, dataClass, factory));
    }

    public <Model, Data> ModelLoader<Model, Data> build(Class<Model> modelClass, Class<Data> dataClass) {
        List<ModelLoader<Model, Data>> loaders = new ArrayList<>();
        for (Entry<?, ?> entry : entries) {
            if (entry.handles(modelClass, dataClass)) {
                loaders.add((ModelLoader<Model, Data>) entry.factory.build(this));
            }
        }
        if (loaders.size() > 1) {
            return new MultiModelLoader<>(loaders);
        } else if (loaders.size() == 1) {
            return loaders.get(0);
        }
        throw new RuntimeException("No match,model: " + modelClass.getName() + ",data:" + dataClass.getName());
    }

    private static class Entry<Model, Data> {
        Class<Model> modelClass;
        Class<Data> dataClass;
        ModelLoader.ModelLoaderFactory<Model, Data> factory;

        public Entry(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory) {
            this.modelClass = modelClass;
            this.dataClass = dataClass;
            this.factory = factory;
        }

        boolean handles(Class<?> modelClass, Class<?> dataClass) {
            return this.modelClass.isAssignableFrom(modelClass) && this.dataClass.isAssignableFrom(dataClass);
        }

        boolean handles(Class<?> modelClass) {
            return this.modelClass.isAssignableFrom(modelClass);
        }
    }
}
