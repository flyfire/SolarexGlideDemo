package com.solarexsoft.solarexglide.request;

import android.widget.ImageView;

import com.solarexsoft.solarexglide.GlideContext;
import com.solarexsoft.solarexglide.Target;

import java.io.File;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 22:33/2020-01-27
 *    Desc:
 * </pre>
 */

public class RequestBuilder {
    private final GlideContext glideContext;
    private final RequestManager requestManager;
    private RequestOptions requestOptions;
    private Object model;

    public RequestBuilder(GlideContext glideContext, RequestManager requestManager) {
        this.glideContext = glideContext;
        this.requestManager = requestManager;
        // todo request options
    }

    public RequestBuilder apply(RequestOptions options) {
        this.requestOptions = options;
        return this;
    }

    public RequestBuilder load(String string) {
        model = string;
        return this;
    }

    public RequestBuilder load(File file) {
        model = file;
        return this;
    }

    public void into(ImageView imageView) {
        Target target = new Target(imageView);
        Request request = new Request(glideContext, model, requestOptions, target);
        requestManager.track(request);
    }
}
