package com.goarchery.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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

        // 解析 SpEL 表达式
        String keyExpression = distributedLock.key();
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        String lockKey = parser.parseExpression(keyExpression).getValue(context, String.class);

        // 这里我用 eventId 作为业务 value（如果方法里没有 eventId 参数，你也可以用 type 或其它参数）
        Object eventIdVal = context.lookupVariable("eventId");

        log.info("方法 [{}] 尝试获取分布式锁 => key: {}, value: {}", methodFullName, lockKey, eventIdVal);

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
            if (locked) {
                lock.unlock();
                log.info("方法 [{}] 释放分布式锁 => key: {}, value: {}", methodFullName, lockKey, eventIdVal);
            }
        }
    }

}

