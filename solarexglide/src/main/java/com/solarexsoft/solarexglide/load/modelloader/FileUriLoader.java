package com.solarexsoft.solarexglide.load.modelloader;

import android.content.ContentResolver;
import android.net.Uri;

import com.solarexsoft.solarexglide.cache.ObjectKey;

import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 10:18/2020-01-22
 *    Desc:
 * </pre>
 */

public class FileUriLoader implements ModelLoader<Uri, InputStream> {
    private final ContentResolver contentResolver;

    public FileUriLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public boolean handles(Uri uri) {
        return ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }

    @Override
    public LoadData<InputStream> buildData(Uri uri) {
        return new LoadData<>(new ObjectKey(uri), new FileUriFetcher(uri, contentResolver));
    }

    public static class Factory implements ModelLoader.ModelLoaderFactory<Uri, InputStream> {

        private final ContentResolver contentResolver;

        public Factory(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
            return new FileUriLoader(contentResolver);
        }
    }
}
