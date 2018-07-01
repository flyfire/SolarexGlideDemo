package com.solarexsoft.solarexglide.load.data.interfaces;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 19:51/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public interface DataFetcher<DATA> {
    interface DataFetcherCallback<DATA> {
        void onFetchReady(DATA data);

        void onFetchFailed(Exception exception);
    }

    void fetchData(DataFetcherCallback<DATA> callback);

    void cancel();

    Class<DATA> getDataClass();
}
