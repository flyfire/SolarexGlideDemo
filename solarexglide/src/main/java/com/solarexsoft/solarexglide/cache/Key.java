package com.solarexsoft.solarexglide.cache;

import java.security.MessageDigest;

/**
 * Created by houruhou at 2018/6/30 22:08
 * Copyright: houruhou,All rights reserved
 */
public interface Key {
    void updateDiskCacheKey(MessageDigest md);
    byte[] getKeyBytes();
}
