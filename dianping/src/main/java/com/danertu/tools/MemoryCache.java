package com.danertu.tools;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class MemoryCache {
    private Map<String, SoftReference<Bitmap>> cache = Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());//软引用
    private static MemoryCache mInstance;

    private MemoryCache() {
    }

    /**
     * 获取MemoryCache实例 ,单例模式
     */
    public static MemoryCache getInstance() {
        if (mInstance == null) {
            mInstance = new MemoryCache();
        }
        return mInstance;
    }

    public Bitmap get(String id) {
        if (!cache.containsKey(id))
            return null;
        SoftReference<Bitmap> ref = cache.get(id);
        return ref.get();
    }

    public void put(String id, Bitmap bitmap) {
        cache.put(id, new SoftReference<>(bitmap));
    }

    public void clear() {
        cache.clear();
    }
}