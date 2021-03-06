package com.solarexsoft.solarexglide.load.engine;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.solarexsoft.solarexglide.cache.Key;
import com.solarexsoft.solarexglide.cache.Resource;
import com.solarexsoft.solarexglide.request.ResourceCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 20:56/2020-01-26
 *    Desc:
 * </pre>
 */

public class EngineJob implements DecodeJob.DecodeCallback {
    private static final String TAG = "EngineJob";

    private Resource resource;
    private static final int MSG_COMPLETE = 1;
    private static final int MSG_EXCEPTION = 2;
    private static final int MSG_CANCELLED = 3;

    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper(), new MainThreadCallback());

    public interface EngineJobListener {
        void onEngineJobComplete(EngineJob engineJob, Key key, Resource resource);

        void onEngineJobCancelled(EngineJob engineJob, Key key);
    }

    private EngineKey key;
    private final List<ResourceCallback> callbacks = new ArrayList<>();
    private final ThreadPoolExecutor threadPoolExecutor;
    private final EngineJobListener listener;
    private boolean isCancelled;
    private DecodeJob decodeJob;

    public EngineJob(ThreadPoolExecutor threadPoolExecutor, EngineJobListener listener, EngineKey key) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.listener = listener;
        this.key = key;
    }

    public void addCallback(ResourceCallback callback) {
        Log.d(TAG, "设置加载状态监听");
        callbacks.add(callback);
    }

    public void removeCallback(ResourceCallback callback) {
        Log.d(TAG, "移除加载状态监听");
        callbacks.remove(callback);
        if (callbacks.isEmpty()) {
            cancel();
        }
    }

    private void cancel() {
        isCancelled = true;
        decodeJob.cancel();
        listener.onEngineJobCancelled(this, key);
    }

    public void start(DecodeJob decodeJob) {
        Log.d(TAG, "开始加载工作");
        this.decodeJob = decodeJob;
        threadPoolExecutor.execute(decodeJob);
    }

    @Override
    public void onResourceReady(Resource resource) {
        this.resource = resource;
        MAIN_THREAD_HANDLER.obtainMessage(MSG_COMPLETE,this).sendToTarget();
    }

    @Override
    public void onResourceLoadFailed(Throwable throwable) {
        MAIN_THREAD_HANDLER.obtainMessage(MSG_EXCEPTION, this).sendToTarget();
    }

    private static class MainThreadCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            EngineJob engineJob = (EngineJob) msg.obj;
            switch (msg.what) {
                case MSG_COMPLETE:
                    engineJob.handleResultOnMainThread();
                    break;
                case MSG_EXCEPTION:
                    engineJob.handleExceptionOnMainThread();
                    break;
                case MSG_CANCELLED:
                    engineJob.handleCancelledOnMainThread();
                    break;
                 default:
                     throw new IllegalStateException("Unrecognized message: " + msg.what);
            }
            return true;
        }
    }

    private void handleCancelledOnMainThread() {
        listener.onEngineJobCancelled(this, key);
        release();
    }

    private void release() {
        callbacks.clear();
        key = null;
        resource = null;
        isCancelled = false;
        decodeJob = null;
    }

    private void handleResultOnMainThread() {
        if (isCancelled) {
            resource.recycle();
            release();
            return;
        }
        resource.acquire();
        listener.onEngineJobComplete(this, key, resource);
        for (ResourceCallback callback : callbacks) {
            resource.acquire();
            callback.onResourceReady(resource);
        }
        resource.release();
        release();
    }

    private void handleExceptionOnMainThread() {
        if (isCancelled) {
            release();
            return;
        }
        listener.onEngineJobComplete(this, key, null);
        for (ResourceCallback callback : callbacks) {
            callback.onResourceReady(null);
        }
    }
}
