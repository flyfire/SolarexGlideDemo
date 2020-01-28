package com.solarexsoft.solarexglide.lifecyclemanager;

import android.support.v4.app.Fragment;

import com.solarexsoft.solarexglide.request.RequestManager;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 22:16/2020-01-27
 *    Desc:
 * </pre>
 */

public class SupportRequestManagerFragment extends Fragment {
    ActivityFragmentLifecycle lifecycle;
    RequestManager requestManager;

    public SupportRequestManagerFragment() {
        this.lifecycle = new ActivityFragmentLifecycle();
    }

    public ActivityFragmentLifecycle getGlideLifecycle() {
        return lifecycle;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
    }
}
