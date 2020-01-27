package com.solarexsoft.solarexglide.lifecyclemanager;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 22:05/2020-01-27
 *    Desc:
 * </pre>
 */

public interface LifecycleListener {

    void onStart();

    void onStop();

    void onDestroy();
}
