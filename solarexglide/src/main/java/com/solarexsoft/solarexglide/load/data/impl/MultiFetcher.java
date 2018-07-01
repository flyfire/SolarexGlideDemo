package com.solarexsoft.solarexglide.load.data.impl;

import com.solarexsoft.solarexglide.load.data.interfaces.DataFetcher;

import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 22:09/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class MultiFetcher<Data> implements DataFetcher<Data>, DataFetcher
        .DataFetcherCallback<Data> {

    private final List<DataFetcher<Data>> mDataFetchers;
    private int mCurrentIndex;
    private DataFetcherCallback<Data> mCallback;

    public MultiFetcher(List<DataFetcher<Data>> dataFetchers) {
        mDataFetchers = dataFetchers;
        mCurrentIndex = 0;
    }

    @Override
    public void onFetchReady(Data data) {
        if (data != null) {
            mCallback.onFetchReady(data);
        } else {
            startNextFetcherOrFail();
        }
    }

    private void startNextFetcherOrFail() {
        if (mCurrentIndex < mDataFetchers.size() - 1 ) {
            mCurrentIndex++;
            fetchData(mCallback);
        } else {
            mCallback.onFetchFailed(new RuntimeException("Fetch failed"));
        }
    }

    @Override
    public void onFetchFailed(Exception exception) {
        startNextFetcherOrFail();
    }

    @Override
    public void fetchData(DataFetcherCallback<Data> callback) {
        mCallback = callback;
        mDataFetchers.get(mCurrentIndex).fetchData(this);
    }

    @Override
    public void cancel() {
        for (DataFetcher<Data> dataFetcher : mDataFetchers) {
            dataFetcher.cancel();
        }
    }

    @Override
    public Class<Data> getDataClass() {
        return mDataFetchers.get(0).getDataClass();
    }
}
