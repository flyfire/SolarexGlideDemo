package com.solarexsoft.solarexglide.load.model.impl;

import android.content.ContentResolver;
import android.net.Uri;

import com.solarexsoft.solarexglide.load.data.impl.FileUriFetcher;
import com.solarexsoft.solarexglide.load.model.ObjectKey;
import com.solarexsoft.solarexglide.load.model.interfaces.ModelLoader;
import com.solarexsoft.solarexglide.load.model.interfaces.ModelLoaderRegistry;

import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 23:29/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class FileUriLoader implements ModelLoader<Uri, InputStream> {

    private final ContentResolver mContentResolver;

    public FileUriLoader(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }


    @Override
    public LoadData<InputStream> buildLoadData(Uri uri) {
        return new LoadData<>(new ObjectKey(uri), new FileUriFetcher(uri, mContentResolver));
    }

    @Override
    public boolean handles(Uri uri) {
        return ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }

    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {
        private final ContentResolver mContentResolver;

        public Factory(ContentResolver contentResolver) {
            mContentResolver = contentResolver;
        }

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry modelLoaderRegistry) {
            return new FileUriLoader(mContentResolver);
        }
    }
}
