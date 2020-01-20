package com.solarexsoft.solarexglide.cache;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 15:26/2020-01-20
 *    Desc:
 * </pre>
 */

public class ActiveResources {
    private ReferenceQueue<Resource> queue;
    private final Resource.ResourceReleaseListener resourceReleaseListener;
    private Map<Key, ResourceWeakReference> activeResources = new HashMap<>();
    private Thread cleanReferenceQueueThread;
    private volatile boolean isShutdown;

    public ActiveResources(Resource.ResourceReleaseListener listener) {
        this.resourceReleaseListener = listener;
    }

    public void activate(Key key, Resource resource) {
        resource.setResourceReleaseListener(key, resourceReleaseListener);
        activeResources.put(key, new ResourceWeakReference(key, resource, getReferenceQueue()));
    }

    public Resource deactivate(Key key) {
        ResourceWeakReference resourceWeakReference = activeResources.remove(key);
        if (resourceWeakReference != null) {
            return resourceWeakReference.get();
        }
        return null;
    }

    public Resource get(Key key) {
        ResourceWeakReference resourceWeakReference = activeResources.get(key);
        if (resourceWeakReference != null) {
            return resourceWeakReference.get();
        }
        return null;
    }

    private ReferenceQueue<Resource> getReferenceQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();
            cleanReferenceQueueThread = new Thread(){
                @Override
                public void run() {
                    while (!isShutdown) {
                        try {
                            ResourceWeakReference remove = (ResourceWeakReference) queue.remove();
                            activeResources.remove(remove.key);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            };
            cleanReferenceQueueThread.start();
        }
        return queue;
    }

    public void shutdownCleanupThread() {
        isShutdown = true;
        if (cleanReferenceQueueThread != null) {
            cleanReferenceQueueThread.interrupt();
            try {
                cleanReferenceQueueThread.join(TimeUnit.SECONDS.toMillis(5));
                if (cleanReferenceQueueThread.isAlive()) {
                    throw new RuntimeException("Failed to join in time.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
