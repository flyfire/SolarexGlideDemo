package com.solarexsoft.solarexglide.facade;

import android.content.Context;

import com.solarexsoft.solarexglide.load.engine.Engine;
import com.solarexsoft.solarexglide.request.RequestOptions;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:47/2020-01-26
 *    Desc:
 * </pre>
 */

public class GlideContext {
    Context context;
    RequestOptions defaultRequestOptions;
    Engine engine;
    Registry registry;

    public GlideContext(Context context, RequestOptions defaultRequestOptions, Engine engine, Registry registry) {
        this.context = context;
        this.defaultRequestOptions = defaultRequestOptions;
        this.engine = engine;
        this.registry = registry;
    }

    public Context getContext() {
        return context;
    }

    public RequestOptions getDefaultRequestOptions() {
        return defaultRequestOptions;
    }

    public Engine getEngine() {
        return engine;
    }

    public Registry getRegistry() {
        return registry;
    }

    public Context getApplicationContext() {
        return context.getApplicationContext();
    }
}
