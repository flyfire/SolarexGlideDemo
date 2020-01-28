package com.solarexsoft.solarexglide.facade;

import android.content.ComponentCallbacks2;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import com.solarexsoft.solarexglide.cache.ArrayPool;
import com.solarexsoft.solarexglide.cache.BitmapPool;
import com.solarexsoft.solarexglide.cache.MemoryCache;
import com.solarexsoft.solarexglide.load.codec.InputStreamBitmapDecoder;
import com.solarexsoft.solarexglide.load.engine.Engine;
import com.solarexsoft.solarexglide.load.modelloader.FileLoader;
import com.solarexsoft.solarexglide.load.modelloader.FileUriLoader;
import com.solarexsoft.solarexglide.load.modelloader.HttpUriLoader;
import com.solarexsoft.solarexglide.load.modelloader.StringLoader;
import com.solarexsoft.solarexglide.request.RequestManager;
import com.solarexsoft.solarexglide.request.RequestManagerRetriever;

import java.io.File;
import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 16:18/2020-01-28
 *    Desc:
 * </pre>
 */

public class Glide implements ComponentCallbacks2 {

    private final MemoryCache memoryCache;
    private final BitmapPool bitmapPool;
    private final ArrayPool arrayPool;
    private final RequestManagerRetriever requestManagerRetriever;
    private final Engine engine;
    private final GlideContext glideContext;

    private static Glide glide;

    public Glide(Context context, GlideBuilder builder) {
        memoryCache = builder.memoryCache;
        bitmapPool = builder.bitmapPool;
        arrayPool = builder.arrayPool;

        Registry registry = new Registry();
        ContentResolver contentResolver = context.getContentResolver();

        registry.add(Uri.class, InputStream.class, new HttpUriLoader.Factory())
                .add(Uri.class, InputStream.class, new FileUriLoader.Factory(contentResolver))
                .add(String.class, InputStream.class, new StringLoader.Factory())
                .add(File.class, InputStream.class, new FileLoader.Factory())
                .register(InputStream.class, new InputStreamBitmapDecoder(bitmapPool, arrayPool));

        engine = builder.engine;
        glideContext = new GlideContext(context, builder.defaultRequestOptions, engine, registry);

        requestManagerRetriever = new RequestManagerRetriever(glideContext);
    }

    private static Glide get(Context context) {
        if (glide == null) {
            synchronized (Glide.class) {
                if (glide == null) {
                    init(context, new GlideBuilder());
                }
            }
        }
        return glide;
    }

    private static void init(Context context, GlideBuilder builder) {
        if (Glide.glide != null) {
            tearDown();
        }
        Context applicationContext = context.getApplicationContext();
        Glide glide = builder.build(applicationContext);
        applicationContext.registerComponentCallbacks(glide);
        Glide.glide = glide;
    }

    private static synchronized void tearDown() {
        if (glide != null) {
            glide.glideContext.getApplicationContext().unregisterComponentCallbacks(glide);
            glide.engine.shutdown();
            glide = null;
        }
    }

    public static RequestManager with(FragmentActivity activity) {
        return Glide.get(activity).requestManagerRetriever.get(activity);
    }

    @Override
    public void onTrimMemory(int level) {
        memoryCache.trimMemory(level);
        bitmapPool.trimMemory(level);
        arrayPool.trimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {
        memoryCache.clearMemory();
        bitmapPool.clearMemory();
        arrayPool.clearMemory();
    }
}
