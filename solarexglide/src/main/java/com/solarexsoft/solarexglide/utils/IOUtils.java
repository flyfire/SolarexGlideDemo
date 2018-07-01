package com.solarexsoft.solarexglide.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by houruhou at 2018/7/1 19:38
 * Copyright: houruhou,All rights reserved
 */
public class IOUtils {
    public static void close(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                        closeable = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (closeable != null) {
                            try {
                                closeable.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
