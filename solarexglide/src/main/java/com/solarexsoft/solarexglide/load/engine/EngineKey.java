package com.solarexsoft.solarexglide.load.engine;

import com.solarexsoft.solarexglide.cache.Key;

import java.security.MessageDigest;
import java.util.Objects;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:43/2020-01-26
 *    Desc:
 * </pre>
 */

public class EngineKey implements Key {

    private final Object model;
    private final int width;
    private final int height;

    public EngineKey(Object model, int width, int height) {
        this.model = model;
        this.width = width;
        this.height = height;
    }

    @Override
    public byte[] getKeyBytes() {
        return toString().getBytes();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest md) {
        md.update(getKeyBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EngineKey)) return false;
        EngineKey engineKey = (EngineKey) o;
        return width == engineKey.width &&
                height == engineKey.height &&
                Objects.equals(model, engineKey.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, width, height);
    }

    @Override
    public String toString() {
        return "EngineKey{" +
                "model=" + model +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
