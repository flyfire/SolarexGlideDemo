package com.solarexsoft.solarexglide.load.modelloader;

import android.net.Uri;

import java.io.File;
import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 10:32/2020-01-22
 *    Desc:
 * </pre>
 */

public class StringLoader implements ModelLoader<String, InputStream> {
    private final ModelLoader<Uri, InputStream> modelLoader;

    public StringLoader(ModelLoader<Uri, InputStream> modelLoader) {
        this.modelLoader = modelLoader;
    }

    @Override
    public boolean handles(String s) {
        return true;
    }

    @Override
    public LoadData<InputStream> buildData(String s) {
        Uri uri = null;
        if (s.startsWith("/")) {
            uri = Uri.fromFile(new File(s));
        } else {
            uri = Uri.parse(s);
        }
        return modelLoader.buildData(uri);
    }

    public static class Factory implements ModelLoader.ModelLoaderFactory<String, InputStream>{

        @Override
        public ModelLoader<String, InputStream> build(ModelLoaderRegistry registry) {
            return new StringLoader(registry.build(Uri.class, InputStream.class));
        }
    }
}
