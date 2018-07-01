package com.solarexsoft.solarexglide.cache.impl;

import android.content.Context;

import com.solarexsoft.solarexglide.cache.disklrucache.DiskLruCache;
import com.solarexsoft.solarexglide.cache.interfaces.DiskCache;
import com.solarexsoft.solarexglide.cache.interfaces.Key;
import com.solarexsoft.solarexglide.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by houruhou at 2018/7/1 17:36
 * Copyright: houruhou,All rights reserved
 */
public class DiskLruCacheWrapper implements DiskCache {
    static final int DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024;
    static final String DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache";

    private MessageDigest mMessageDigest;
    private DiskLruCache mDiskLruCache;

    public DiskLruCacheWrapper(Context context) {
        this(new File(context.getCacheDir(), DEFAULT_DISK_CACHE_DIR), DEFAULT_DISK_CACHE_SIZE);
    }

    protected DiskLruCacheWrapper(File directory, long maxsize) {
        try {
            mMessageDigest = MessageDigest.getInstance("SHA-256");
            // 打开缓存目录
            // directory
            // appversion: app版本号，版本号变化时，缓存数据会被清除
            // valuecount: 同一个key可以对应多少文件
            // maxsize
            mDiskLruCache = DiskLruCache.open(directory, 1, 1, maxsize);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateKey(Key key) {
        key.updateDiskCacheKey(mMessageDigest);
        return Utils.sha256BytesToHex(mMessageDigest.digest());
    }

    @Override
    public File get(Key key) {
        String k = generateKey(key);
        File result = null;
        try {
            DiskLruCache.Value value = mDiskLruCache.get(k);
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
        String k = generateKey(key);
        try {
            DiskLruCache.Value value = mDiskLruCache.get(k);
            if (value != null) {
                return;
            }
            DiskLruCache.Editor editor = mDiskLruCache.edit(k);
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
        String k = generateKey(key);
        try {
            mDiskLruCache.remove(k);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDiskLruCache = null;
        }
    }
}
