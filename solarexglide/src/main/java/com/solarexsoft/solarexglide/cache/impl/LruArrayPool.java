package com.solarexsoft.solarexglide.cache.impl;

import android.util.LruCache;

import com.solarexsoft.solarexglide.cache.interfaces.ArrayPool;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by houruhou at 2018/7/1 11:49
 * Copyright: houruhou,All rights reserved
 */
public class LruArrayPool implements ArrayPool {
    public static final int ARRAY_POOL_SIZE_BYTES = 4 * 1024 * 1024;

    private final int mMaxSize;
    private LruCache<Integer, byte[]> mCache;
    private final NavigableMap<Integer, Integer> mSortedSizes = new TreeMap<>();

    // 单个资源与maxSize最大比例
    private static final int SINGLE_ARRAY_MAX_SIZE_DIVISOR = 2;
    // 溢出大小
    private static final int MAX_OVER_SIZE_MULTIPLE = 8;

    public LruArrayPool() {
        this(ARRAY_POOL_SIZE_BYTES);
    }

    public LruArrayPool(int maxSize) {
        this.mMaxSize = maxSize;
        this.mCache = new LruCache<Integer, byte[]>(maxSize) {
            @Override
            protected int sizeOf(Integer key, byte[] value) {
                return value.length;
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, byte[] oldValue, byte[]
                    newValue) {
                mSortedSizes.remove(oldValue.length);
            }
        };
    }

    @Override
    public byte[] get(int len) {
        // 获取等于或大于len的key
        Integer key = mSortedSizes.ceilingKey(len);
        if (null != key) {
            // 缓存中的大小只能比需要的内存大小溢出8倍
            if (key <= (MAX_OVER_SIZE_MULTIPLE * len)) {
                byte[] bytes = mCache.remove(key);
                mSortedSizes.remove(key);
                return bytes == null ? new byte[len] : bytes;
            }
        }
        return new byte[len];
    }

    @Override
    public void put(byte[] data) {
        int length = data.length;
        // 太大了，不缓存
        if (!isSmallEnoughForReuse(length)) {
            return;
        }
        mSortedSizes.put(length, 1);
        mCache.put(length, data);
    }

    private boolean isSmallEnoughForReuse(int length) {
        return length <= mMaxSize / SINGLE_ARRAY_MAX_SIZE_DIVISOR;
    }

    @Override
    public void clearMemory() {
        mCache.evictAll();
    }

    @Override
    public void trimMemory(int level) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            clearMemory();
        } else if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN
                || level == android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            mCache.trimToSize(mMaxSize / 2);
        }
    }

    @Override
    public int getMaxSize() {
        return mMaxSize;
    }
}
