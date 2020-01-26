package com.solarexsoft.solarexglide.request;

import com.solarexsoft.solarexglide.cache.Resource;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 21:02/2020-01-26
 *    Desc:
 * </pre>
 */

public interface ResourceCallback {
    void onResourceReady(Resource resource);
}
