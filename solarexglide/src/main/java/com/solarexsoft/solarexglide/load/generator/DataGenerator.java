package com.solarexsoft.solarexglide.load.generator;

import com.solarexsoft.solarexglide.cache.Key;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:35/2020-01-26
 *    Desc:
 * </pre>
 */

public interface DataGenerator {

    interface DataGeneratorCallback {
        enum DataSource {
            REMOTE,
            CACHE;
        }

        void onDataFetcherReady(Key sourceKey, Object data, DataSource dataSource);

        void onDataFetcherFailed(Key sourceKey, Exception e);
    }

    boolean startNext();

    void cancel();
}
