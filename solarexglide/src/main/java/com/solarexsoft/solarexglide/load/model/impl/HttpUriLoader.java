package com.solarexsoft.solarexglide.load.model.impl;

import android.net.Uri;

import com.solarexsoft.solarexglide.load.data.impl.HttpUriFetcher;
import com.solarexsoft.solarexglide.load.model.ObjectKey;
import com.solarexsoft.solarexglide.load.model.interfaces.ModelLoader;
import com.solarexsoft.solarexglide.load.model.interfaces.ModelLoaderRegistry;

import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 23:15/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class HttpUriLoader implements ModelLoader<Uri, InputStream> {
    @Override
    public LoadData<InputStream> buildLoadData(Uri uri) {
        return new LoadData<>(new ObjectKey(uri), new HttpUriFetcher(uri));
    }

    @Override
    public boolean handles(Uri uri) {
        String scheme = uri.getScheme();
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }

    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {
        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry modelLoaderRegistry) {
            // 真正的加载者，不是代理类
            return new HttpUriLoader();
        }
    }
}
