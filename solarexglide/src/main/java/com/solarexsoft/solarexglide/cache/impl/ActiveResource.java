package com.solarexsoft.solarexglide.cache.impl;

import com.solarexsoft.solarexglide.cache.interfaces.Key;
import com.solarexsoft.solarexglide.cache.interfaces.Resource;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by houruhou at 2018/7/1 12:27
 * Copyright: houruhou,All rights reserved
 * 正在使用的图片资源
 */
public class ActiveResource {

    private ReferenceQueue<Resource> mReferenceQueue;
    private final Resource.ResourceReleaseListener mResourceReleaseListener;
    private Map<Key, ResourceWeakReference> mActiveResources = new HashMap<>();
    private Thread mCleanReferenceQueueThread;
    private boolean isShutdown;


    public ActiveResource(Resource.ResourceReleaseListener listener) {
        this.mResourceReleaseListener = listener;
    }

    // 加入活动缓存
    public void activate(Key key, Resource resource) {
        resource.setResourceReleaseListener(key, mResourceReleaseListener);
        mActiveResources.put(key, new ResourceWeakReference(key, resource, getReferenceQueue()));
    }

    // 移除活动缓存
    public  Resource deactivate(Key key) {
        ResourceWeakReference reference = mActiveResources.remove(key);
        if (reference != null) {
            return reference.get();
        }
        return null;
    }

    // 获得活动缓存
    public Resource get(Key key) {
        ResourceWeakReference reference = mActiveResources.get(key);
        if (reference != null) {
            return reference.get();
        }
        return null;
    }

    /*
     * 引用队列，通知弱引用被回收了
     */
    private ReferenceQueue<Resource> getReferenceQueue() {
        if (null == mReferenceQueue) {
            mReferenceQueue = new ReferenceQueue<>();
            mCleanReferenceQueueThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isShutdown) {
                        try {
                             // 阻塞调用，获取被回收对象的WeakReference
                             ResourceWeakReference remove = (ResourceWeakReference) mReferenceQueue.remove();
                             mActiveResources.remove(remove.key);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mCleanReferenceQueueThread.start();
        }
        return mReferenceQueue;
    }

    public void shutdown() {
        isShutdown = true;
        if (mCleanReferenceQueueThread != null) {
            mCleanReferenceQueueThread.interrupt();
            try {
                mCleanReferenceQueueThread.join(TimeUnit.SECONDS.toMillis(5));
                if (mCleanReferenceQueueThread.isAlive()) {
                    throw new RuntimeException("Failed to join in time.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static final class ResourceWeakReference extends WeakReference<Resource> {
        final Key key;
        public ResourceWeakReference(Key key, Resource referent, ReferenceQueue<? super Resource> q) {
            super(referent, q);
            this.key = key;
        }
    }
}
