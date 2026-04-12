package com.ratelimiter.FluxWard.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.model.enums.KeyType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


import java.time.Clock;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@EnableConfigurationProperties(RateLimiterProperties.class)
public class RateLimiterConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public Cache<String, AtomicLong> fallBackCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, java.util.concurrent.TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public RateLimitRule defaultRule(RateLimiterProperties properties) {
        return new RateLimitRule(
                properties.getCapacity(),
                properties.getRefillRatePerSecond(),
                properties.getWindowMs(),
                KeyType.valueOf(properties.getKeyType())
        );
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
