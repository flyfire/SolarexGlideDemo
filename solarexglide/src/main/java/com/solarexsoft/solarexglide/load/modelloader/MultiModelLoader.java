package com.solarexsoft.solarexglide.load.modelloader;

import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 09:43/2020-01-22
 *    Desc:
 * </pre>
 */

public class MultiModelLoader<Model, Data> implements ModelLoader<Model, Data> {

    private final List<ModelLoader<Model, Data>> modelLoaders;

    public MultiModelLoader(List<ModelLoader<Model, Data>> modelLoaders) {
        this.modelLoaders = modelLoaders;
    }

    @Override
    public boolean handles(Model model) {
        for (ModelLoader<Model, Data> modelLoader : modelLoaders) {
            if (modelLoader.handles(model)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LoadData<Data> buildData(Model model) {
        for (ModelLoader<Model, Data> modelLoader : modelLoaders) {
            if (modelLoader.handles(model)) {
                LoadData<Data> loadData = modelLoader.buildData(model);
                return loadData;
            }
        }
        return null;
    }
}
