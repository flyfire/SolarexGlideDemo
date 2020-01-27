package com.solarexsoft.solarexglide.request;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 21:09/2020-01-27
 *    Desc:
 * </pre>
 */

public class RequestOptions {
    private int errorId;
    private int placeholderId;
    private int overrideHeight = -1;
    private int overrideWidth = -1;

    public RequestOptions placeholder(int placeholderId) {
        this.placeholderId = placeholderId;
        return this;
    }

    public RequestOptions error(int errorId) {
        this.errorId = errorId;
        return this;
    }

    public RequestOptions override(int width, int height) {
        this.overrideWidth = width;
        this.overrideHeight = height;
        return this;
    }

    public int getErrorId() {
        return errorId;
    }

    public int getPlaceholderId() {
        return placeholderId;
    }

    public int getOverrideHeight() {
        return overrideHeight;
    }

    public int getOverrideWidth() {
        return overrideWidth;
    }
}
