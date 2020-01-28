package com.solarexsoft.solarexglide.request;

import android.graphics.drawable.Drawable;

import com.solarexsoft.solarexglide.facade.GlideContext;
import com.solarexsoft.solarexglide.cache.Resource;
import com.solarexsoft.solarexglide.load.engine.Engine;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 21:43/2020-01-27
 *    Desc:
 * </pre>
 */

public class Request implements Target.SizeReadyCallback, ResourceCallback {

    private enum Status {
        PENDING,
        RUNNING,
        WAITING_FOR__SIZE,
        COMPLETE,
        FAILED,
        CANCELLED,
        CLEARED,
        PAUSED;
    }

    private Engine engine;
    private GlideContext glideContext;
    private Object model;
    private RequestOptions requestOptions;
    private Target target;
    private Resource resource;
    private Engine.LoadStatus loadStatus;
    private Status status;
    private Drawable errorDrawable;
    private Drawable placeholderDrawable;

    public Request(GlideContext glideContext, Object model, RequestOptions requestOptions, Target target) {
        this.glideContext = glideContext;
        this.model = model;
        this.requestOptions = requestOptions;
        this.target = target;
        // todo engine
        status = Status.PENDING;
    }

    public void recycle() {
        glideContext = null;
        model = null;
        requestOptions = null;
        target = null;
        loadStatus = null;
        errorDrawable = null;
        placeholderDrawable = null;
    }

    public void begin() {
        status = Status.WAITING_FOR__SIZE;
        target.onLoadStarted(getPlaceholderDrawable());
        if (requestOptions.getOverrideWidth() > 0 && requestOptions.getOverrideHeight() > 0) {
            onSizeReady(requestOptions.getOverrideWidth(), requestOptions.getOverrideHeight());
        } else {
            target.getSize(this);
        }
    }

    public void cancel() {
        target.cancel();
        status = Status.CANCELLED;
        if (loadStatus != null) {
            loadStatus.cancel();
            loadStatus = null;
        }
    }

    public void clear() {
        if (status == Status.CLEARED) {
            return;
        }
        cancel();
        if (resource != null) {
            releaseResource(resource);
        }
        status = Status.CLEARED;
    }

    public boolean isPaused() {
        return status == Status.PAUSED;
    }

    public void pause() {
        clear();
        status = Status.PAUSED;
    }

    private void releaseResource(Resource resource) {
        resource.release();
        this.resource = null;
    }

    public boolean isRunning() {
        return status == Status.RUNNING || status == Status.WAITING_FOR__SIZE;
    }

    public boolean isComplete() {
        return status == Status.COMPLETE;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED || status == Status.CLEARED;
    }


    private Drawable getErrorDrawable() {
        if (errorDrawable == null && requestOptions.getErrorId() > 0) {
            errorDrawable = loadDrawable(requestOptions.getErrorId());
        }
        return errorDrawable;
    }

    private Drawable getPlaceholderDrawable() {
        if (placeholderDrawable == null && requestOptions.getPlaceholderId() > 0) {
            placeholderDrawable = loadDrawable(requestOptions.getPlaceholderId());
        }
        return placeholderDrawable;
    }

    private Drawable loadDrawable(int errorId) {
        // todo glide context
        return null;
    }

    private void setErrorPlaceholder() {
        Drawable error = getErrorDrawable();
        if (error == null) {
            error = getPlaceholderDrawable();
        }
        target.onLoadFailed(error);
    }

    @Override
    public void onSizeReady(int width, int height) {
        status = Status.RUNNING;
        loadStatus = engine.load(glideContext, model, width, height, this);
    }

    @Override
    public void onResourceReady(Resource resource) {
        loadStatus = null;
        this.resource = resource;
        if (resource == null) {
            status = Status.FAILED;
            setErrorPlaceholder();
            return;
        }
        target.onResourceReady(resource.getBitmap());
    }
}
