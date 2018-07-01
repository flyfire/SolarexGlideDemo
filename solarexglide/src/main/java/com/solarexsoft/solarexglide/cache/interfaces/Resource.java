package com.solarexsoft.solarexglide.cache.interfaces;

import android.graphics.Bitmap;

/**
 * Created by houruhou at 2018/6/30 22:10
 * Copyright: houruhou,All rights reserved
 */
public class Resource {
    private Bitmap bitmap;

    //引用计数
    private int acquired;
    private Key key;
    private ResourceReleaseListener listener;

    // 引用计数为0时，回调 onResourceReleased
    public interface ResourceReleaseListener {
        void onResourceReleased(Key key,Resource resource);
    }

    public Resource(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setResourceReleaseListener(Key key,ResourceReleaseListener listener) {
        this.key = key;
        this.listener = listener;
    }

    // 引用计数增加
    public void acquire() {
        if (bitmap.isRecycled()) {
            throw new IllegalStateException("Acquire a recycled resource");
        }
        ++acquired;
    }

    // 引用计数减少
    public void release() {
        if (--acquired == 0) {
            listener.onResourceReleased(key, this);
        }
    }

    // 释放
    public void recycle() {
        if (acquired > 0) {
            return;
        }
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
