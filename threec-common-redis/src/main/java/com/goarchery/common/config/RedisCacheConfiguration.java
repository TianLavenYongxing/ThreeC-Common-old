package com.goarchery.common.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.spring.cache.CacheConfig;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfiguration {

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        // 定义不同 cacheName 的 TTL 配置
        Map<String, CacheConfig> config = new HashMap<>();
        // 默认缓存 12 小时，最大空闲时间 30 分钟
        config.put("CompCategoryContent:isAllowMod", new CacheConfig(12 * 60 * 60 * 1000L, 30 * 60 * 1000L));
        config.put("user", new CacheConfig(6 * 60 * 60 * 1000L, 10 * 60 * 1000L));

        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
