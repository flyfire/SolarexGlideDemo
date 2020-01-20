package com.solarexsoft.solarexglide.cache;

import android.content.Context;

import com.solarexsoft.solarexglide.cache.disklrucache.DiskLruCache;
import com.solarexsoft.solarexglide.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 16:10/2020-01-20
 *    Desc:
 * </pre>
 */

public class DiskLruCacheWrapper implements DiskCache {
    static final int DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024;
    static final String DEFAULT_DISK_CACHE_DIR = "solarex_glide_disk_cache";

    private MessageDigest md;
    private DiskLruCache diskLruCache;

    public DiskLruCacheWrapper(Context context) {
        this(new File(context.getCacheDir(), DEFAULT_DISK_CACHE_DIR), DEFAULT_DISK_CACHE_SIZE);
    }

    protected DiskLruCacheWrapper(File dir, long maxsize) {
        try {
            md = MessageDigest.getInstance("SHA-256");
            diskLruCache = DiskLruCache.open(dir, 1, 1, maxsize);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getKeyString(Key key) {
        key.updateDiskCachKey(md);
        return new String(Utils.sha256BytesToHex(md.digest()));
    }

    @Override
    public File get(Key key) {
        String keyStr = getKeyString(key);
        File result = null;
        try {
            DiskLruCache.Value value = diskLruCache.get(keyStr);
            if (value != null) {
                result = value.getFile(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void put(Key key, Writer writer) {
        String keyStr = getKeyString(key);
        try {
            DiskLruCache.Value value = diskLruCache.get(keyStr);
            if (value != null) {
                return;
            }
            DiskLruCache.Editor editor = diskLruCache.edit(keyStr);
            try {
                File file = editor.getFile(0);
                if (writer.write(file)) {
                    editor.commit();
                }
            } finally {
                editor.abortUnlessCommitted();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Key key) {
        String keyStr = getKeyString(key);
        try {
            diskLruCache.remove(keyStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        try {
            diskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            diskLruCache = null;
        }
    }
}
