package com.solarexsoft.solarexglide.cache.impl;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.solarexsoft.solarexglide.cache.interfaces.BitmapPool;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by houruhou at 2018/7/1 19:13
 * Copyright: houruhou,All rights reserved
 */
public class LruBitmapPool extends LruCache<Integer, Bitmap> implements BitmapPool {
    private boolean isRemoved;

    // 找到合适内存大小的bitmap
    NavigableMap<Integer, Integer> mSortedSize = new TreeMap<>();

    private static final int MAX_OVER_SIZE_MULTIPLE = 2;

    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    // 将bitmap放入复用池
    @Override
    public void put(Bitmap bitmap) {
        if (!bitmap.isMutable()) {
            bitmap.recycle();
            return;
        }
        int size = bitmap.getAllocationByteCount();
        if (size >= maxSize()) {
            bitmap.recycle();
            return;
        }
        put(size, bitmap);
        mSortedSize.put(size, 0);
    }

    // 获取一个可以复用的bitmap
    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        // 只关心ARGB_8888和RGB_656格式
        // 新的bitmap需要的内存大小
        int size = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);
        // 获取复用池bitmap大小大于等于size的key
        Integer key = mSortedSize.ceilingKey(size);
        // 为了避免内存浪费，限定可复用bitmap的大小小于请求内存大小的MAX_OVER_SIZE_MULTIPLE倍
        if (key != null && key <= size * MAX_OVER_SIZE_MULTIPLE) {
            // 调用remove会回调entryRemoved，主动调用remove的时候不要recycle bitmap
            isRemoved = true;
            Bitmap bitmap = remove(key);
            isRemoved = false;
            return bitmap;
        }
        return null;
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return value.getAllocationByteCount();
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        mSortedSize.remove(key);
        // 如果是由于内存问题回收bitmap，调用bitmap.recycle()否则就是主动从复用池中复用bitmap，不调用bitmap.recycle
        if (!isRemoved) {
            oldValue.recycle();
        }
    }
}
