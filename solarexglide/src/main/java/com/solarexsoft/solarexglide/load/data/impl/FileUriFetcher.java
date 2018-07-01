package com.solarexsoft.solarexglide.load.data.impl;

import android.content.ContentResolver;
import android.net.Uri;

import com.solarexsoft.solarexglide.load.data.interfaces.DataFetcher;
import com.solarexsoft.solarexglide.utils.IOUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 19:55/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class FileUriFetcher implements DataFetcher<InputStream> {

    private final Uri mUri;
    private final ContentResolver mContentResolver;

    public FileUriFetcher(Uri uri, ContentResolver contentResolver) {
        mUri = uri;
        mContentResolver = contentResolver;
    }

    @Override
    public void fetchData(DataFetcherCallback<InputStream> callback) {
        InputStream is = null;
        try {
            is = mContentResolver.openInputStream(mUri);
            callback.onFetchReady(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.onFetchFailed(e);
        } finally {
            if (null != is) {
                IOUtils.close(is);
            }
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}
