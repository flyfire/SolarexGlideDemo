package com.solarexsoft.solarexglide.cache;

import java.security.MessageDigest;
import java.util.Objects;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 10:14/2020-01-22
 *    Desc:
 * </pre>
 */

public class ObjectKey implements Key {
    private final Object object;

    public ObjectKey(Object object) {
        this.object = object;
    }

    @Override
    public byte[] getKeyBytes() {
        return object.toString().getBytes();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest md) {
        md.update(getKeyBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectKey)) return false;
        ObjectKey objectKey = (ObjectKey) o;
        return Objects.equals(object, objectKey.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object);
    }
}
