package com.solarexsoft.solarexglide.cache;


import android.support.v4.util.LruCache;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:21/2020-01-22
 *    Desc:
 * </pre>
 */

public class LruArrayPool implements ArrayPool {
    public static final int ARRAY_POOL_SIZE_BYTES = 4 * 1024 * 1024;

    private final int maxSize;
    private final LruCache<Integer, byte[]> cache;
    private final NavigableMap<Integer, Integer> sortedSize = new TreeMap<>();

    // 单个资源与maxSize的最大比例
    private static final int SINGLE_ARRAY_MAX_SIZE_DIVISOR = 2;
    // 溢出大小
    private static final int MAX_OVER_SIZE_MULTIPLE = 8;

    public LruArrayPool(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LruCache<Integer, byte[]>(maxSize){
            @Override
            protected int sizeOf(Integer key, byte[] value) {
                return value.length;
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, byte[] oldValue, byte[] newValue) {
                sortedSize.remove(oldValue.length);
            }
        };
    }

    @Override
    public byte[] get(int len) {
        Integer key = sortedSize.ceilingKey(len);
        if (key != null) {
            if (key <= (len * MAX_OVER_SIZE_MULTIPLE)) {
                byte[] bytes = cache.remove(key);
                sortedSize.remove(key);
                return bytes == null ? new byte[len] : bytes;
            }
        }
        return new byte[len];
    }

    @Override
    public void put(byte[] data) {
        int len = data.length;
        if (data.length >= maxSize / SINGLE_ARRAY_MAX_SIZE_DIVISOR) {
            return;
        }
        sortedSize.put(len, 0);
        cache.put(len, data);
    }

    @Override
    public void clearMemory() {
        this.cache.evictAll();
        this.sortedSize.clear();
    }

    @Override
    public void trimMemory(int level) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            clearMemory();
        } else if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN
                || level == android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            cache.trimToSize(maxSize / 2);
        }
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }
}
