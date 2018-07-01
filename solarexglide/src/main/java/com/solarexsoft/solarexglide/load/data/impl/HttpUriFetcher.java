package com.solarexsoft.solarexglide.load.data.impl;

import android.net.Uri;

import com.solarexsoft.solarexglide.load.data.interfaces.DataFetcher;
import com.solarexsoft.solarexglide.utils.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 20:01/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class HttpUriFetcher implements DataFetcher<InputStream> {

    private final Uri mUri;
    private boolean isCanceled;

    public HttpUriFetcher(Uri uri) {
        mUri = uri;
    }


    @Override
    public void fetchData(DataFetcherCallback<InputStream> callback) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(mUri.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            is = conn.getInputStream();
            int responseCode = conn.getResponseCode();
            if (isCanceled) {
                return;
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                callback.onFetchReady(is);
            } else {
                callback.onFetchFailed(new RuntimeException(conn.getResponseMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFetchFailed(e);
        } finally {
            if (null != is) {
                IOUtils.close(is);
            }
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    @Override
    public void cancel() {
        isCanceled = true;
    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}
