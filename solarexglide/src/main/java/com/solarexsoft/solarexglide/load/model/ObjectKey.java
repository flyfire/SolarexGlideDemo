package com.solarexsoft.solarexglide.load.model;

import com.solarexsoft.solarexglide.cache.interfaces.Key;

import java.security.MessageDigest;
import java.util.Objects;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 21:29/2018/7/1
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class ObjectKey implements Key {
    private final Object mObject;

    public ObjectKey(Object object) {
        mObject = object;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest md) {
        md.update(mObject.toString().getBytes());
    }

    @Override
    public byte[] getKeyBytes() {
        return mObject.toString().getBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectKey)) return false;
        ObjectKey objectKey = (ObjectKey) o;
        return Objects.equals(mObject, objectKey.mObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mObject);
    }
}
