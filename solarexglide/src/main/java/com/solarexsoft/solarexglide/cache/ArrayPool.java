package com.solarexsoft.solarexglide.cache;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 13:37/2020-01-20
 *    Desc:
 * </pre>
 */

public interface ArrayPool {

    byte[] get(int len);

    void put(byte[] data);

    void clearMemory();

    void trimMemory(int level);

    int getMaxSize();
}
