package com.ratelimiter.FluxWard.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ratelimiter.FluxWard.core.AlgorithmFactory;
import com.ratelimiter.FluxWard.core.RouteRuleResolver;
import com.ratelimiter.FluxWard.core.algorithm.FixedWindowRateLimiter;
import com.ratelimiter.FluxWard.core.algorithm.SlidingWindowRateLimiter;
import com.ratelimiter.FluxWard.core.algorithm.TokenBucketRateLimiter;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.model.enums.KeyType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;


import java.time.Clock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@EnableConfigurationProperties(RateLimiterProperties.class)
public class RateLimiterConfig {

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public Cache<String, AtomicLong> fallBackCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    @Bean
    public RateLimitRule defaultRule(RateLimiterProperties properties) {
        return new RateLimitRule(
                properties.getCapacity(),
                properties.getRefillRatePerSecond(),
                properties.getWindowMs(),
                RateLimitRule.KeyType.valueOf(properties.getKeyType()),
                properties.getAlgorithm()
        );
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public AlgorithmFactory algorithmFactory(TokenBucketRateLimiter tokenBucket, FixedWindowRateLimiter fixedWindow,
                                             SlidingWindowRateLimiter slidingWindow) {
        return new AlgorithmFactory(tokenBucket, fixedWindow, slidingWindow);
    }

    @Bean
    public RouteRuleResolver routeRuleResolver(RateLimiterProperties properties, RateLimitRule defaultRule) {
        System.out.println("Routes in config: " + properties.getRoutes().size());
        return new RouteRuleResolver(properties, defaultRule);
    }

}
