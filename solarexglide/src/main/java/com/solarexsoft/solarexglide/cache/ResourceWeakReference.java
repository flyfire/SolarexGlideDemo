package com.solarexsoft.solarexglide.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 15:32/2020-01-20
 *    Desc:
 * </pre>
 */

public class ResourceWeakReference extends WeakReference<Resource> {
    public final Key key;
    public ResourceWeakReference(Key key, Resource referent, ReferenceQueue<? super Resource> q) {
        super(referent, q);
        this.key = key;
    }
}
