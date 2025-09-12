package com.goarchery.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * 锁的 key，支持 SpEL 表达式，例如：#eventId
     */
    String key();

    /**
     * 租约时间（自动释放时间）
     */
    long leaseTime() default 5;

    /**
     * 时间单位
     */
    TimeUnit unit() default TimeUnit.MINUTES;
}
