package com.solarexsoft.solarexglide.cache.interfaces;

import android.graphics.Bitmap;

/**
 * Created by houruhou at 2018/7/1 11:44
 * Copyright: houruhou,All rights reserved
 */
public interface BitmapPool {
    void put(Bitmap bitmap);

    /*
     * 获取一个可复用的Bitmap
     */
    Bitmap get(int width, int height, Bitmap.Config config);
}
