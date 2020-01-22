package com.solarexsoft.solarexglide.load.modelloader;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 09:54/2020-01-22
 *    Desc:
 * </pre>
 */

public class HttpUriFetcher implements DataFetcher<InputStream> {

    private final Uri uri;
    private boolean isCanceled;

    public HttpUriFetcher(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void loadData(DataFetcherCallback<? super InputStream> callback) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(uri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            inputStream = connection.getInputStream();
            int responseCode = connection.getResponseCode();
            if (isCanceled) {
                return;
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                callback.onFetcherReady(inputStream);
            } else {
                callback.onFetcherFailed(new RuntimeException(connection.getResponseMessage()));
            }
        } catch (Exception e) {
            callback.onFetcherFailed(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public void cancel() {
        isCanceled = false;
    }

    @Override
    public Class<?> getDataClass() {
        return InputStream.class;
    }
}
