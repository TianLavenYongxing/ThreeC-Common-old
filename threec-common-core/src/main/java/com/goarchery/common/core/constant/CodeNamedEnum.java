package com.goarchery.common.core.constant;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 统一的“带 code/name”的枚举接口：
 * - 提供 fromCode / fromName / fromString 的通用静态方法
 * - 内置线程安全缓存，避免每次遍历枚举常量
 * - 不使用 computeIfAbsent，规避泛型推断/通配符捕获问题
 */
public interface CodeNamedEnum {
    Integer getCode();
    String getName();

    // ================= 对外通用 API =================

    /**
     * 按 code 查找（O(1)）
     */
    static <E extends Enum<E> & CodeNamedEnum> E fromCode(Class<E> type, int code) {
        Map<Integer, E> map = codeCache(type);
        E e = map.get(code);
        if (e == null) throw new IllegalArgumentException(err("code", String.valueOf(code), type));
        return e;
    }

    /**
     * 按 name 查找（大小写不敏感，O(1)）
     */
    static <E extends Enum<E> & CodeNamedEnum> E fromName(Class<E> type, String name) {
        if (name == null) return null;
        Map<String, E> map = nameCache(type);
        E e = map.get(norm(name));
        if (e == null) throw new IllegalArgumentException(err("name", name, type));
        return e;
    }

    /**
     * 智能解析：优先按 code（数字），再按 name（不区分大小写），最后按常量名（不区分大小写）
     */
    static <E extends Enum<E> & CodeNamedEnum> E fromString(Class<E> type, String source) {
        if (source == null || source.isBlank()) return null;
        String s = source.trim();

        // 1) 尝试按 code
        try {
            return fromCode(type, Integer.parseInt(s));
        } catch (NumberFormatException ignored) {}

        // 2) 尝试按 name
        E byName = nameCache(type).get(norm(s));
        if (byName != null) return byName;

        // 3) 尝试按常量名
        try {
            return Enum.valueOf(type, s.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {}

        throw new IllegalArgumentException(err("value", s, type));
    }

    // ================= 内部缓存 =================
    // value 使用 ?，存取时在受控位置做一次受检强转（调用方通过类型边界保证类型安全）

    ConcurrentMap<Class<?>, Map<Integer, ?>> CODE_CACHE = new ConcurrentHashMap<>();
    ConcurrentMap<Class<?>, Map<String,  ?>> NAME_CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E> & CodeNamedEnum> Map<Integer, E> codeCache(Class<E> type) {
        Map<Integer, ?> exist = CODE_CACHE.get(type);
        if (exist == null) {
            Map<Integer, E> built = buildByCode(type);
            Map<Integer, ?> prev = CODE_CACHE.putIfAbsent(type, built);
            exist = (prev != null) ? prev : built;
        }
        return (Map<Integer, E>) exist;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E> & CodeNamedEnum> Map<String, E> nameCache(Class<E> type) {
        Map<String, ?> exist = NAME_CACHE.get(type);
        if (exist == null) {
            Map<String, E> built = buildByName(type);
            Map<String, ?> prev = NAME_CACHE.putIfAbsent(type, built);
            exist = (prev != null) ? prev : built;
        }
        return (Map<String, E>) exist;
    }

    private static <E extends Enum<E> & CodeNamedEnum> Map<Integer, E> buildByCode(Class<E> type) {
        Map<Integer, E> m = new HashMap<>();
        for (E e : type.getEnumConstants()) {
            Integer c = e.getCode();
            if (c != null) m.put(c, e);
        }
        return Collections.unmodifiableMap(m);
    }

    private static <E extends Enum<E> & CodeNamedEnum> Map<String, E> buildByName(Class<E> type) {
        Map<String, E> m = new HashMap<>();
        for (E e : type.getEnumConstants()) {
            String n = e.getName();
            if (n != null) m.put(norm(n), e);
        }
        return Collections.unmodifiableMap(m);
    }

    // ================= 工具方法 =================

    private static String norm(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }

    private static String err(String kind, String val, Class<?> type) {
        return "Invalid " + kind + ": " + val + " for " + type.getSimpleName();
    }

    /**
     * 可选：提供清理缓存的方法（例如单测或热更新字典时）
     */
    static void clearEnumCaches() {
        CODE_CACHE.clear();
        NAME_CACHE.clear();
    }
}
