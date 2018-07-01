package com.solarexsoft.solarexglide.cache;

import java.io.File;

/**
 * Created by houruhou at 2018/6/30 23:01
 * Copyright: houruhou,All rights reserved
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
