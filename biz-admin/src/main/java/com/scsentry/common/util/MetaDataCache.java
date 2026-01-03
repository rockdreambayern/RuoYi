package com.scsentry.common.util;

import com.ruoyi.common.exception.base.BaseException;

import java.util.Map;

public class MetaDataCache {

    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static void put(Map<String, Object> map) {
        if (threadLocal.get() == null) {
            threadLocal.set(map);
        } else {
            throw new BaseException("元数据缓存不支持多次更新");
        }
    }

    public static Object get(String key) {
        if (threadLocal.get() == null) {
            return null;
        }
        return threadLocal.get().getOrDefault(key, null);
    }
}
