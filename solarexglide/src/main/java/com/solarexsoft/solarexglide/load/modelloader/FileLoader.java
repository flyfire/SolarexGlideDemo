package com.solarexsoft.solarexglide.load.modelloader;

import android.net.Uri;

import java.io.File;
import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 10:23/2020-01-22
 *    Desc:
 * </pre>
 */

public class FileLoader implements ModelLoader<File, InputStream> {
    private final ModelLoader<Uri, InputStream> modelLoader;

    public FileLoader(ModelLoader<Uri, InputStream> modelLoader) {
        this.modelLoader = modelLoader;
    }

    @Override
    public boolean handles(File file) {
        return true;
    }

    @Override
    public LoadData<InputStream> buildData(File file) {
        return modelLoader.buildData(Uri.fromFile(file));
    }

    public static class Factory implements ModelLoader.ModelLoaderFactory<File, InputStream> {

        @Override
        public ModelLoader<File, InputStream> build(ModelLoaderRegistry registry) {
            return new FileLoader(registry.build(Uri.class, InputStream.class));
        }
    }
}
