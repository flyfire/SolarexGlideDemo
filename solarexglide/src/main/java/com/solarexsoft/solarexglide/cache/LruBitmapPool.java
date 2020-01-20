package com.solarexsoft.solarexglide.cache;

import android.content.ComponentCallbacks2;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 16:42/2020-01-20
 *    Desc:
 * </pre>
 */

public class LruBitmapPool extends LruCache<Integer, Bitmap> implements BitmapPool {

    private boolean isRemoveVoluntarily;
    NavigableMap<Integer, Integer> sortedSize = new TreeMap<>();
    private static final int MAX_OVER_SIZE_MULTIPLE = 2;

    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return value.getAllocationByteCount();
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        sortedSize.remove(key);
        if (!isRemoveVoluntarily) {
            oldValue.recycle();
        }
    }

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
        sortedSize.put(size, 0);
    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        int size = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);
        Integer key = sortedSize.ceilingKey(size);
        if (key != null && key < size * MAX_OVER_SIZE_MULTIPLE) {
            isRemoveVoluntarily = true;
            Bitmap remove = remove(key);
            isRemoveVoluntarily = false;
            return remove;
        }
        return null;
    }

    @Override
    public void clearMemory() {
        evictAll();
    }

    @Override
    public void trimMemory(int level) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            clearMemory();
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            trimToSize(maxSize()/2);
        }
    }
}
