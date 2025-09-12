package com.goarchery.common.config;


import com.goarchery.common.redis.NoKeyEnum;
import com.goarchery.common.redis.RedisKeyPrefix;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * 在方法层面声明基于 Redisson 的分布式锁。
 *
 * <p><b>锁键拼装规则：</b>
 * <ol>
 *   <li>确定前缀：优先使用 {@link #prefix()}，若为空则使用 {@link #keyEnum()}（取其枚举常量的 {@code prefix()} 值）；都为空则无前缀。</li>
 *   <li>计算动态部分：解析 {@link #key()} 的 SpEL 表达式（可为空）。</li>
 *   <li>使用 {@link #delimiter()} 将“前缀”和“动态部分”连接；两端若为空则忽略空段。</li>
 *   <li>若最终键仍为空，切面将回退到“类名.方法名”。</li>
 * </ol>
 *
 * <p><b>参数名要求：</b> SpEL 通过方法参数名注入上下文，请确保编译选项保留参数名（如使用 {@code -parameters}）。</p>
 *
 * <p><b>示例：</b></p>
 * <pre>{@code
 * // 1) 字符串前缀 + SpEL（常用）
 * @DistributedLock(prefix = "ga:content:init", key = "#type.name + ':' + #eventId", leaseTime = 10)
 * public void reset(Type type, Long eventId) {}
 * // => 锁键: ga:content:init:RESET:123
 *
 * // 2) 仅字符串前缀（静态全局锁）
 * @DistributedLock(prefix = "ga:content:init")
 * public void warmUp() {}
 * // => 锁键: ga:content:init
 *
 * // 3) 自定义分隔符
 * @DistributedLock(prefix = "ga:content:init", key = "#type.name + '-' + #eventId", delimiter = "|")
 * public void doJob(Type type, Long eventId) {}
 * // => 锁键: ga:content:init|RESET-123
 *
 * // 4) 单常量枚举前缀 + SpEL（推荐枚举方式）
 * @DistributedLock(keyEnum = ContentInitKey.class, key = "#type.name + ':' + #eventId")
 * public void run(Type type, Long eventId) {}
 * // => 锁键: ga:content:init:RESET:123
 *
 * // 5) 同时设置 prefix 与 keyEnum（以 prefix 为准）
 * @DistributedLock(prefix = "ga:override", keyEnum = ContentInitKey.class, key = "#eventId")
 * public void override(Long eventId) {}
 * // => 锁键: ga:override:123
 *
 * // 6) 仅 SpEL（无前缀，不推荐全局使用）
 * @DistributedLock(key = "#type.name + ':' + #eventId")
 * public void adhoc(Type type, Long eventId) {}
 * // => 锁键: RESET:123
 *
 * // 7) Elvis 兜底（空值安全）
 * @DistributedLock(prefix = "ga:content:init", key = "(#type?.name?:'NA') + ':' + (#eventId?:'NA')")
 * public void safe(Type type, Long eventId) {}
 * // => 锁键: ga:content:init:RESET:123
 * }</pre>
 *
 * @author GoArchery
 * @since 1.0
 */
public @interface DistributedLock {

    /**
     * 动态部分，支持 SpEL，比如："#type.name + ':' + #eventId"
     * 若留空，则只使用前缀来作为锁键。
     */
    String key() default "";

    /**
     * 静态前缀（若与 keyEnum 同时设置，以 prefix 优先）。
     * 例："ga:content:reset"
     */
    String prefix() default "";

    /**
     * 使用枚举提供前缀，保证编译期类型安全。
     * 当 prefix 非空时忽略该值。
     */
    Class<? extends RedisKeyPrefix> keyEnum() default NoKeyEnum.class;

    /**
     * 拼接前缀与 SpEL 的分隔符。
     */
    String delimiter() default ":";

    /**
     * 租约时间（自动释放）。
     */
    long leaseTime() default 5;

    /**
     * 时间单位。
     */
    TimeUnit unit() default TimeUnit.MINUTES;



}
