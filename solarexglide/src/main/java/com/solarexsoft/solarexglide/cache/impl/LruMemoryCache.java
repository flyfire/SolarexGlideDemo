package com.solarexsoft.solarexglide.cache.impl;

import android.os.Build;
import android.support.v4.util.LruCache;

import com.solarexsoft.solarexglide.cache.interfaces.Key;
import com.solarexsoft.solarexglide.cache.interfaces.MemoryCache;
import com.solarexsoft.solarexglide.cache.interfaces.Resource;

/**
 * Created by houruhou at 2018/7/1 12:18
 * Copyright: houruhou,All rights reserved
 */
public class LruMemoryCache extends LruCache<Key, Resource> implements MemoryCache {
    private ResourceRemoveListener mListener;
    private boolean isRemoved;


    public LruMemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(Key key, Resource value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return value.getBitmap().getAllocationByteCount();
        }
        return value.getBitmap().getByteCount();
    }

    @Override
    protected void entryRemoved(boolean evicted, Key key, Resource oldValue, Resource newValue) {
        // 给复用池使用
        if (null != mListener && null != oldValue && !isRemoved) {
            mListener.onResourceRemoved(oldValue);
        }
    }

    @Override
    public void setResourceRemoveListener(ResourceRemoveListener listener) {
        this.mListener = listener;
    }

    @Override
    public Resource delete(Key key) {
        // 主动删除不会回调 mListener.onResourceRemoved
        isRemoved = true;
        Resource removed = remove(key);
        isRemoved = false;
        return removed;
    }
}
