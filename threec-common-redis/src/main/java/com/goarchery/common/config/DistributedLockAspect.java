package com.goarchery.common.config;

import com.goarchery.common.redis.NoKeyEnum;
import com.goarchery.common.redis.RedisKeyPrefix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodFullName = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        // 构建 SpEL 上下文：参数名 -> 参数值
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            if (paramNames != null && i < paramNames.length) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // 解析前缀：prefix > keyEnum > ""
        String prefix = distributedLock.prefix();
        if (isBlank(prefix)) {
            Class<? extends RedisKeyPrefix> enumClass = distributedLock.keyEnum();
            if (enumClass != null && enumClass != NoKeyEnum.class) {
                RedisKeyPrefix[] constants = enumClass.getEnumConstants();
                if (constants != null && constants.length > 0) {
                    // 若一个枚举中有多个常量，建议一个枚举只代表一个前缀（更清晰）
                    prefix = constants[0].prefix();
                }
            }
        }

        // 解析 SpEL（允许为空）
        String spel = distributedLock.key();
        String evaluated = "";
        if (!isBlank(spel)) {
            Expression exp = parser.parseExpression(spel);
            evaluated = exp.getValue(context, String.class);
        }

        // 拼接最终锁键
        String delimiter = Objects.toString(distributedLock.delimiter(), ":");
        String lockKey = joinNonBlank(delimiter, prefix, evaluated);

        // 兜底：防止空键导致所有线程争抢一个同名锁
        if (isBlank(lockKey)) {
            lockKey = methodFullName;
        }

        Object eventIdVal = context.lookupVariable("eventId");
        log.info("方法 [{}] 获取分布式锁 => key: {}", methodFullName, lockKey);
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(0, distributedLock.leaseTime(), distributedLock.unit());
            if (!locked) {
                log.warn("方法 [{}] 获取分布式锁失败 => key: {}, value: {}", methodFullName, lockKey, eventIdVal);
                throw new RuntimeException("获取锁失败，请稍后重试");
            }
            log.info("方法 [{}] 成功获取分布式锁 => key: {}, value: {}", methodFullName, lockKey, eventIdVal);
            return joinPoint.proceed();
        } finally {
            // 只在“当前线程持有锁”时解锁，防止非法解锁异常
            if (locked && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                    log.info("方法 [{}] 释放分布式锁 => key: {}, value: {}", methodFullName, lockKey, eventIdVal);
                } catch (Exception e) {
                    log.error("方法 [{}] 解锁异常 => key: {}, err: {}", methodFullName, lockKey, e.getMessage(), e);
                }
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String joinNonBlank(String delimiter, String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!isBlank(p)) {
                if (!sb.isEmpty()) sb.append(delimiter);
                sb.append(p);
            }
        }
        return sb.toString();
    }
}
