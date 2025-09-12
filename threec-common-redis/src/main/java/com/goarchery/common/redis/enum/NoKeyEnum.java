package com.goarchery.common.redis;

/** 注解默认值使用的占位枚举。 */
public enum NoKeyEnum implements RedisKeyPrefix {
    NONE;
    @Override public String prefix() { return ""; }
}