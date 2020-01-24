package com.solarexsoft.solarexglide.load.codec;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.solarexsoft.solarexglide.cache.ArrayPool;
import com.solarexsoft.solarexglide.cache.BitmapPool;

import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:26/2020-01-24
 *    Desc:
 * </pre>
 */

public class InputStreamBitmapDecoder implements ResourceDecoder<InputStream> {

    private final BitmapPool bitmapPool;
    private final ArrayPool arrayPool;

    public InputStreamBitmapDecoder(BitmapPool bitmapPool, ArrayPool arrayPool) {
        this.bitmapPool = bitmapPool;
        this.arrayPool = arrayPool;
    }

    @Override
    public boolean handles(InputStream source) throws IOException {
        return true;
    }

    @Override
    public Bitmap decode(InputStream source, int width, int height) throws IOException {
        MarkInputStream markInputStream;
        if (source instanceof MarkInputStream) {
            markInputStream = (MarkInputStream) source;
        } else {
            markInputStream = new MarkInputStream(source, arrayPool);
        }
        markInputStream.mark(0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(source, null, options);
        options.inJustDecodeBounds = false;

        int sourceWidth = options.outWidth;
        int sourceHeight = options.outHeight;

        int targetWidth = width < 0 ? sourceWidth : width;
        int targetHeight = height < 0 ? sourceHeight : height;

        float widthFactor = targetWidth * 1.0f / sourceWidth;
        float heightFactor = targetHeight * 1.0f / sourceHeight;

        float factor = Math.max(widthFactor, heightFactor);

        int outWidth = Math.round(factor * sourceWidth);
        int outHeight = Math.round(factor * sourceHeight);

        int widthScaleFactor = sourceWidth % outWidth == 0 ? sourceWidth/outWidth : sourceWidth/outWidth + 1;
        int heightScaleFactor = sourceHeight % outHeight == 0 ? sourceHeight/outHeight : sourceHeight/outHeight + 1;

        int sampleSize = Math.max(widthScaleFactor, heightScaleFactor);
        sampleSize = Math.max(1, sampleSize);

        options.inSampleSize = sampleSize;

        options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = bitmapPool.get(outWidth, outHeight, Bitmap.Config.RGB_565);

        options.inBitmap = bitmap;
        options.inMutable = true;

        markInputStream.reset();

        Bitmap result = BitmapFactory.decodeStream(markInputStream, null, options);
        markInputStream.release();
        return result;
    }
}
