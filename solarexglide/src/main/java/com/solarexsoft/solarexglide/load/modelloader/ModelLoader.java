package com.solarexsoft.solarexglide.load.modelloader;

import com.solarexsoft.solarexglide.cache.Key;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 09:10/2020-01-22
 *    Desc:
 * </pre>
 */

public interface ModelLoader<Model, Data> {

    class LoadData<Data> {
        public final Key key;
        public final DataFetcher<Data> fetcher;

        public LoadData(Key key, DataFetcher<Data> fetcher) {
            this.key = key;
            this.fetcher = fetcher;
        }
    }

    boolean handles(Model model);

    LoadData<Data> buildData(Model model);

    interface ModelLoaderFactory<Model, Data> {
        ModelLoader<Model, Data> build(ModelLoaderRegistry registry);
    }
}
