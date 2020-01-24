package com.solarexsoft.solarexglide.load.codec;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:24/2020-01-24
 *    Desc:
 * </pre>
 */

public interface ResourceDecoder<T> {
    boolean handles(T source) throws IOException;
    Bitmap decode(T source, int width, int height) throws IOException;
}
