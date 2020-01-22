package com.solarexsoft.solarexglide.load.modelloader;

import android.net.Uri;

import com.solarexsoft.solarexglide.cache.ObjectKey;

import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 10:09/2020-01-22
 *    Desc:
 * </pre>
 */

public class HttpUriLoader implements ModelLoader<Uri, InputStream> {
    @Override
    public boolean handles(Uri uri) {
        String scheme = uri.getScheme();
        return scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https");
    }

    @Override
    public LoadData<InputStream> buildData(Uri uri) {
        return new LoadData<>(new ObjectKey(uri), new HttpUriFetcher(uri));
    }

    public static class Factory implements ModelLoader.ModelLoaderFactory<Uri, InputStream> {

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
            return new HttpUriLoader();
        }
    }
}
