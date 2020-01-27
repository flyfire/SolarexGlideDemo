package com.solarexsoft.solarexglide.lifecyclemanager;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 22:08/2020-01-27
 *    Desc:
 * </pre>
 */

public class ApplicationLifecycle implements Lifecycle {
    @Override
    public void addListener(LifecycleListener listener) {
        listener.onStart();
    }

    @Override
    public void removeListener(LifecycleListener listener) {

    }
}
