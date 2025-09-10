package com.goarchery.common.core.utils;

import com.goarchery.common.core.constant.CodeNamedEnum;

import java.util.Locale;

public final class CodeNamedEnumUtils {
    private CodeNamedEnumUtils() {}

    public static <E extends Enum<E> & CodeNamedEnum> E from(String source, Class<E> type) {
        if (source == null || source.isBlank()) return null;
        String s = source.trim();

        // 1) code
        try {
            int v = Integer.parseInt(s);
            for (E e : type.getEnumConstants()) {
                Integer code = e.getCode();
                if (code != null && code == v) return e;
            }
        } catch (NumberFormatException ignored) {}

        // 2) name（大小写不敏感）
        for (E e : type.getEnumConstants()) {
            String name = e.getName();
            if (name != null && name.equalsIgnoreCase(s)) return e;
        }

        // 3) 枚举常量名
        String upper = s.toUpperCase(Locale.ROOT);
        for (E e : type.getEnumConstants()) if (e.name().equals(upper)) return e;

        throw new IllegalArgumentException("Invalid enum value: " + s + " for " + type.getSimpleName());
    }
}
