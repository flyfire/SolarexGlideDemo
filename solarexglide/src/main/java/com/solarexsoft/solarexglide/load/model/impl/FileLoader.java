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
 *    CreatAt: 23:09/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class FileLoader<Data> implements ModelLoader<File, Data> {

    private final ModelLoader<Uri, Data> mLoader;

    public FileLoader(ModelLoader<Uri, Data> loader) {
        mLoader = loader;
    }

    @Override
    public LoadData<Data> buildLoadData(File file) {
        return mLoader.buildLoadData(Uri.fromFile(file));
    }

    @Override
    public boolean handles(File file) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<File, InputStream> {

        @Override
        public ModelLoader<File, InputStream> build(ModelLoaderRegistry modelLoaderRegistry) {
            // 需要构造代理对象
            return new FileLoader<>(modelLoaderRegistry.build(Uri.class, InputStream.class));
        }
    }
}
