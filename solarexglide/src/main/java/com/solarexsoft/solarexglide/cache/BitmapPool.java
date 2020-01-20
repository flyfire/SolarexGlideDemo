package com.solarexsoft.solarexglide.cache;

import android.graphics.Bitmap;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 14:50/2020-01-20
 *    Desc:
 * </pre>
 */

public interface BitmapPool {

    void put(Bitmap bitmap);

    Bitmap get(int width, int height, Bitmap.Config config);

    void clearMemory();

    void trimMemory(int level);
}
