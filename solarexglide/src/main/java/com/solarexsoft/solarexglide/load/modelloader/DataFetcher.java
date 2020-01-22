package com.solarexsoft.solarexglide.load.modelloader;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 09:13/2020-01-22
 *    Desc:
 * </pre>
 */

public interface DataFetcher<Data> {

    interface DataFetcherCallback<Data> {
        void onFetcherReady(Data data);

        void onFetcherFailed(Exception ex);
    }

    void loadData(DataFetcherCallback<? super Data> callback);

    void cancel();

    Class<?> getDataClass();
}
