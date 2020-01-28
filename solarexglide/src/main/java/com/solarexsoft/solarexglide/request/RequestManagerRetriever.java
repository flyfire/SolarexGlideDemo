package com.solarexsoft.solarexglide.request;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.solarexsoft.solarexglide.facade.GlideContext;
import com.solarexsoft.solarexglide.lifecyclemanager.ApplicationLifecycle;
import com.solarexsoft.solarexglide.lifecyclemanager.SupportRequestManagerFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 15:44/2020-01-28
 *    Desc:
 * </pre>
 */

public class RequestManagerRetriever implements Handler.Callback {

    public static final String FRAG_TAG = "solarex_glide_fragment";
    private static final int REMOVE_SUPPORT_FRAGMENT = 100;
    private final GlideContext glideContext;
    RequestManager applicationRequestManager;

    private Map<FragmentManager, SupportRequestManagerFragment> supportRequestManagerFragmentMap = new HashMap<>();
    private Handler handler;

    public RequestManagerRetriever(GlideContext glideContext) {
        this.glideContext = glideContext;
        handler = new Handler(Looper.getMainLooper(), this);
    }

    private RequestManager getApplicationRequestManager() {
        if (applicationRequestManager == null) {
            applicationRequestManager = new RequestManager(new ApplicationLifecycle(), glideContext);
        }
        return applicationRequestManager;
    }

    public RequestManager get(Context context) {
        if (!(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return get((FragmentActivity)context);
            } else if (context instanceof ContextWrapper) {
                get(((ContextWrapper)context).getBaseContext());
            }
        }
        return getApplicationRequestManager();
    }

    private RequestManager get(FragmentActivity activity) {
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        return supportFragmentGet(supportFragmentManager);
    }

    private RequestManager supportFragmentGet(FragmentManager supportFragmentManager) {
        SupportRequestManagerFragment fragment = getSupportRequestManagerFragment(supportFragmentManager);
        RequestManager requestManager = fragment.getRequestManager();
        if (requestManager == null) {
            requestManager = new RequestManager(fragment.getGlideLifecycle(), glideContext);
            fragment.setRequestManager(requestManager);
        }
        return requestManager;
    }

    private SupportRequestManagerFragment getSupportRequestManagerFragment(FragmentManager supportFragmentManager) {
        SupportRequestManagerFragment fragment = (SupportRequestManagerFragment) supportFragmentManager.findFragmentByTag(FRAG_TAG);
        if (fragment == null) {
            fragment = supportRequestManagerFragmentMap.get(supportFragmentManager);
            if (fragment == null) {
                fragment = new SupportRequestManagerFragment();
                supportRequestManagerFragmentMap.put(supportFragmentManager, fragment);
                supportFragmentManager.beginTransaction().add(fragment, FRAG_TAG).commitAllowingStateLoss();
                handler.obtainMessage(REMOVE_SUPPORT_FRAGMENT, supportFragmentManager).sendToTarget();
            }
        }
        return fragment;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case REMOVE_SUPPORT_FRAGMENT:
                FragmentManager fragmentManager = (FragmentManager) msg.obj;
                supportRequestManagerFragmentMap.remove(fragmentManager);
                break;
        }
        return false;
    }
}
