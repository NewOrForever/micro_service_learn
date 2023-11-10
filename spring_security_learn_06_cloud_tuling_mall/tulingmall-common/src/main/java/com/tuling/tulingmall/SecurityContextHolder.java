package com.tuling.tulingmall;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName:SecurityContextHolder
 * Package:com.tuling.tulingmall
 * Description: 线程变量
 *
 * @Date:2023/11/8 16:28
 * @Author:qs@1.com
 */
public class SecurityContextHolder {
    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = getLocalMap();
        map.put(key, value == null ? "" : value);
    }

    public static String get(String key) {
        Map<String, Object> map = getLocalMap();
        return map.getOrDefault(key, "").toString();
    }

    public static <T> T get(String key, Class<T> clazz) {
        Map<String, Object> map = getLocalMap();
        return (T) map.getOrDefault(key, null);
    }

    public static Map<String, Object> get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    private static Map<String, Object> getLocalMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }
}
