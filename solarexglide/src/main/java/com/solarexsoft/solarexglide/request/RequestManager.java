package com.solarexsoft.solarexglide.request;

import com.solarexsoft.solarexglide.GlideContext;
import com.solarexsoft.solarexglide.lifecyclemanager.Lifecycle;
import com.solarexsoft.solarexglide.lifecyclemanager.LifecycleListener;

import java.io.File;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 15:31/2020-01-28
 *    Desc:
 * </pre>
 */

public class RequestManager implements LifecycleListener {

    private final Lifecycle lifecycle;
    private final GlideContext glideContext;

    RequestTracker requestTracker;

    public RequestManager(Lifecycle lifecycle, GlideContext glideContext) {
        this.lifecycle = lifecycle;
        this.glideContext = glideContext;
        requestTracker = new RequestTracker();
        this.lifecycle.addListener(this);
    }

    @Override
    public void onStart() {
        requestTracker.resumeRequests();
    }

    @Override
    public void onStop() {
        requestTracker.pauseRequests();
    }

    @Override
    public void onDestroy() {
        lifecycle.removeListener(this);
        requestTracker.clearRequests();
    }

    public void track(Request request) {
        requestTracker.runRequest(request);
    }

    public RequestBuilder load(String str) {
        return new RequestBuilder(glideContext, this).load(str);
    }

    public RequestBuilder load(File file) {
        return new RequestBuilder(glideContext, this).load(file);
    }
}
