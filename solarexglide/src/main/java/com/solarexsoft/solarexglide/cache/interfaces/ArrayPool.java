package com.solarexsoft.solarexglide.cache.interfaces;

/**
 * Created by houruhou at 2018/6/30 22:54
 * Copyright: houruhou,All rights reserved
 */
public interface ArrayPool {
    byte[] get(int len);
    void put(byte[] data);

    void clearMemory();
    void trimMemory(int level);
    int getMaxSize();
}
