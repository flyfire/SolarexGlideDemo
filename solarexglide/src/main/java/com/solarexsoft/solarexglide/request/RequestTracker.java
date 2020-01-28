package com.solarexsoft.solarexglide.request;

import com.solarexsoft.solarexglide.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 22:23/2020-01-27
 *    Desc:
 * </pre>
 */

public class RequestTracker {

    private Set<Request> requests = Collections.newSetFromMap(new WeakHashMap<Request, Boolean>());
    private List<Request> pendingRequests = new ArrayList<>();

    private boolean isPaused;

    public void runRequest(Request request) {
        requests.add(request);
        if (!isPaused) {
            request.begin();
        } else {
            pendingRequests.add(request);
        }
    }

    public void pauseRequests() {
        isPaused = true;
        for (Request request : Utils.getSnapshot(requests)) {
            if (request.isRunning()) {
                request.pause();
                pendingRequests.add(request);
            }
        }
    }

    public void resumeRequests() {
        isPaused = false;
        for (Request request : Utils.getSnapshot(requests)) {
            if (!request.isComplete() && !request.isCancelled() && !request.isRunning()) {
                request.begin();
            }
        }
        pendingRequests.clear();
    }

    public void clearRequests() {
        for (Request request : Utils.getSnapshot(requests)) {
            if (request == null) {
                return;
            }
            requests.remove(request);
            request.clear();
            request.recycle();
        }
        pendingRequests.clear();
    }
}
