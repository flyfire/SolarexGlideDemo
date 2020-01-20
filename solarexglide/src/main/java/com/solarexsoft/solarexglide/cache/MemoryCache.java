package com.solarexsoft.solarexglide.cache;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 13:31/2020-01-20
 *    Desc:
 * </pre>
 */

public interface MemoryCache {

    Resource put(Key key, Resource resource);

    Resource removeVoluntarily(Key key);

    void setResourceRemoveListener(ResourceRemoveListener listener);

    void clearMemory();

    void trimMemory(int level);

    interface ResourceRemoveListener {
        void onResourceRemoved(Resource resource);
    }
}
