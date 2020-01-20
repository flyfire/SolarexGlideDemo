package com.solarexsoft.solarexglide.cache;

import android.content.ComponentCallbacks2;
import android.os.Build;
import android.support.v4.util.LruCache;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 15:03/2020-01-20
 *    Desc:
 * </pre>
 */

public class LruMemoryCache extends LruCache<Key, Resource> implements MemoryCache {

    private ResourceRemoveListener listener;

    private boolean isRemoveVoluntarily;

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
        if (!isRemoveVoluntarily && listener != null && oldValue != null) {
            // LRU 达到maxSize 之后由LRU被动移除多余元素，放入LruBitmapPool中，复用Bitmap内存
            // 避免 BitmapFactory decode bitmap时多次申请内存导致的内存抖动和内存碎片问题
            listener.onResourceRemoved(oldValue);
        }
    }

    @Override
    public Resource removeVoluntarily(Key key) {
        // 主动移除元素，放入 ActiveResources 中
        isRemoveVoluntarily = true;
        Resource remove = remove(key);
        isRemoveVoluntarily = false;
        return remove;
    }

    @Override
    public void setResourceRemoveListener(ResourceRemoveListener listener) {
        this.listener = listener;
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
