package com.solarexsoft.solarexglide.cache;

import java.security.MessageDigest;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 13:33/2020-01-20
 *    Desc:
 * </pre>
 */

public interface Key {

    byte[] getKeyBytes();

    void updateDiskCacheKey(MessageDigest md);
}
