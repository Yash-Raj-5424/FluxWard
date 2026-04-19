package com.ratelimiter.FluxWard.core.algorithm;

import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.store.RateLimitStore;

import java.time.Clock;
import java.time.Instant;

public class SlidingWindowRateLimiter {

    private final RateLimitStore store;
    private final Clock clock;

    public SlidingWindowRateLimiter(RateLimitStore store, Clock clock) {
        this.store = store;
        this.clock = clock;
    }

    @Override
    public RateLimitResult tryAcquire(String clientKey, RateLimitRule rule) {
        long now = clock.millis();
        long windowStart = now - rule.getWindowMs();
        return store.getAndIncrement(clientKey, rule, Instant.now(clock));
    }
}
