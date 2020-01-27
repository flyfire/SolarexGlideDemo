package com.solarexsoft.solarexglide.lifecyclemanager;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 22:06/2020-01-27
 *    Desc:
 * </pre>
 */

public interface Lifecycle {

    void addListener(LifecycleListener listener);

    void removeListener(LifecycleListener listener);
}
