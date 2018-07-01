package com.solarexsoft.solarexglide.load.model.interfaces;

import com.solarexsoft.solarexglide.cache.interfaces.Key;
import com.solarexsoft.solarexglide.load.data.interfaces.DataFetcher;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 21:55/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public interface ModelLoader<Model, Data> {
    interface ModelLoaderFactory<Model, Data> {
        // 本来直接无参构造一个ModelLoader即可，但是可能有多个ModelLoader对应相应的Model和Data
        // 所以增加一层MultiModelLoader，并将ModelLoaderRegistry传入以构建MultiModelLoader
        ModelLoader<Model, Data> build(ModelLoaderRegistry modelLoaderRegistry);
    }

    class LoadData<Data> {
        public final Key mKey;
        public final DataFetcher<Data> mDataFetcher;

        public LoadData(Key key, DataFetcher<Data> dataFetcher) {
            mKey = key;
            mDataFetcher = dataFetcher;
        }
    }

    LoadData<Data> buildLoadData(Model model);

    boolean handles(Model model);

}
