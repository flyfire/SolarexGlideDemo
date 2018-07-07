package com.solarexsoft.solarexglide.load.codec;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.solarexsoft.solarexglide.cache.interfaces.ArrayPool;
import com.solarexsoft.solarexglide.cache.interfaces.BitmapPool;

import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 21:45/2018/7/7
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class StreamBitmapDecoder implements ResourceDecoder<InputStream> {
    private final BitmapPool mBitmapPool;
    private final ArrayPool mArrayPool;

    public StreamBitmapDecoder(BitmapPool bitmapPool, ArrayPool arrayPool) {
        mBitmapPool = bitmapPool;
        mArrayPool = arrayPool;
    }

    @Override
    public boolean handles(InputStream source) throws IOException {
        return true;
    }

    @Override
    public Bitmap decode(InputStream source, int width, int height) throws IOException {
        MarkInputStream is;
        if (source instanceof MarkInputStream) {
            is = (MarkInputStream) source;
        } else {
            is = new MarkInputStream(source, mArrayPool);
        }
        is.mark(0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        options.inJustDecodeBounds = false;

        int originWidth = options.outWidth;
        int originHeight = options.outHeight;

        int targetWidth = width<0?originWidth:width;
        int targetHeight = height<0?originHeight:height;

        float widthFactor = targetWidth/(float)originWidth;
        float heightFactor = targetHeight/(float)originHeight;
        float factor = Math.max(widthFactor, heightFactor);

        int outWidth = Math.round(factor * originWidth);
        int outHeight = Math.round(factor * originHeight);

        int widthScaleFactor = originWidth%outWidth==0?originWidth/outWidth:originWidth/outWidth+1;
        int heightScaleFactor = originHeight%outHeight==0?originHeight/outHeight:originHeight/outHeight+1;

        int sampleSize = Math.max(widthScaleFactor, heightScaleFactor);
        sampleSize = Math.max(1, sampleSize);

        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = mBitmapPool.get(outWidth, outHeight, Bitmap.Config.RGB_565);
        options.inBitmap = bitmap;
        options.inMutable = true;

        is.reset();
        Bitmap result = BitmapFactory.decodeStream(is, null, options);
        is.release();

        return result;
    }
}
