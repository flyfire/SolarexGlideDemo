package com.solarexsoft.solarexglide.load.codec;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 21:43/2018/7/7
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public interface ResourceDecoder<T> {
    boolean handles(T source) throws IOException;
    Bitmap decode(T source, int width, int height) throws IOException;
}
