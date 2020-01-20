package com.solarexsoft.solarexglide.cache;

import java.io.File;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 13:35/2020-01-20
 *    Desc:
 * </pre>
 */

public interface DiskCache {

    interface Writer {
        boolean write(File file);
    }

    File get(Key key);

    void put(Key key, Writer writer);

    void delete(Key key);

    void clear();
}
