package com.ratelimiter.FluxWard.service;

import com.ratelimiter.FluxWard.config.RateLimiterProperties;
import com.ratelimiter.FluxWard.core.RateLimiter;
import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.store.InMemoryFallBackStore;
import com.ratelimiter.FluxWard.store.RateLimitStore;
import com.ratelimiter.FluxWard.store.RedisRateLimitStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RateLimiter rateLimiter;
    private final RedisRateLimitStore redisStore;
    private final InMemoryFallBackStore fallBackStore;
    private final RateLimitRule defaultRule;
    private final RateLimiterProperties properties;

    public RateLimitResult check(String clientKey){
        RateLimitStore store = redisStore.isAvailabe()
                ? redisStore
                : fallBackStore;

        if (!redisStore.isAvailabe() && !properties.getFailOpen()) {
            return RateLimitResult.rejected(5_000L, Instant.now().plusSeconds(5));
        }

        return store.getAndIncrement(clientKey, defaultRule, Instant.now());
    }
}
