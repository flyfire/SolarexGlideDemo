package com.solarexsoft.solarexglide.load.model.impl;

import android.net.Uri;

import com.solarexsoft.solarexglide.load.model.interfaces.ModelLoader;
import com.solarexsoft.solarexglide.load.model.interfaces.ModelLoaderRegistry;

import java.io.File;
import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 23:21/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class StringLoader<Data> implements ModelLoader<String, Data> {
    // 代理
    private final ModelLoader<Uri, Data> mLoader;

    public StringLoader(ModelLoader<Uri, Data> loader) {
        mLoader = loader;
    }

    @Override
    public LoadData<Data> buildLoadData(String s) {
        Uri uri = null;
        if (s.startsWith("/")) {
            uri = Uri.fromFile(new File(s));
        } else {
            uri = Uri.parse(s);
        }
        return mLoader.buildLoadData(uri);
    }

    @Override
    public boolean handles(String s) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<String,InputStream> {
        @Override
        public ModelLoader<String, InputStream> build(ModelLoaderRegistry modelLoaderRegistry) {
            return new StringLoader<>(modelLoaderRegistry.build(Uri.class, InputStream.class));
        }
    }
}
