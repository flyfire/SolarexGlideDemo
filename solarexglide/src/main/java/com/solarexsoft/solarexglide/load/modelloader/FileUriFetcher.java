package com.solarexsoft.solarexglide.load.modelloader;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 09:49/2020-01-22
 *    Desc:
 * </pre>
 */

public class FileUriFetcher implements DataFetcher<InputStream> {

    private final Uri uri;
    private final ContentResolver contentResolver;

    public FileUriFetcher(Uri uri, ContentResolver contentResolver) {
        this.uri = uri;
        this.contentResolver = contentResolver;
    }

    @Override
    public void loadData(DataFetcherCallback<? super InputStream> callback) {
        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            callback.onFetcherReady(inputStream);
        } catch (FileNotFoundException e) {
            callback.onFetcherFailed(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public Class<?> getDataClass() {
        return InputStream.class;
    }
}
