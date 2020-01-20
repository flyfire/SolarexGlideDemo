package com.solarexsoft.solarexglide.cache;

import android.graphics.Bitmap;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 14:53/2020-01-20
 *    Desc:
 * </pre>
 */

public class Resource {

    private Bitmap bitmap;

    private int acquired;

    private ResourceReleaseListener listener;

    private Key key;

    public Resource(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setResourceReleaseListener(Key key, ResourceReleaseListener listener) {
        this.key = key;
        this.listener = listener;
    }

    public void acquire() {
        if (bitmap.isRecycled()) {
            throw new IllegalStateException("Acquire a recycled resource");
        }
        ++acquired;
    }

    public void release() {
        if (--acquired == 0) {
            listener.onResourceReleased(key,this);
        }
    }

    public void recycle() {
        if (acquired > 0) {
            return;
        }
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /*
      acquired = 0 时回调
     */
    public interface ResourceReleaseListener {
        void onResourceReleased(Key key, Resource resource);
    }
}
