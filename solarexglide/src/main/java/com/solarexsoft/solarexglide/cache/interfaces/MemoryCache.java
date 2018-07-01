package com.solarexsoft.solarexglide.cache.interfaces;

/**
 * Created by houruhou at 2018/6/30 22:58
 * Copyright: houruhou,All rights reserved
 */
public interface MemoryCache {
    interface ResourceRemoveListener {
        void onResourceRemoved(Resource resource);
    }

    Resource put(Key key, Resource resource);

    void setResourceRemoveListener(ResourceRemoveListener listener);

    Resource delete(Key key);
}
