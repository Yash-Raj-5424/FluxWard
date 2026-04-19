package com.ratelimiter.FluxWard.core.algorithm;

import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.store.RateLimitStore;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class FixedWindowRateLimiter {

    private final RateLimitStore store;
    private final Clock clock;

    public FixedWindowRateLimiter(RateLimitStore store, Clock clock) {
        this.store = store;
        this.clock = clock;
    }

    @Override
    public RateLimitResult tryAcquire(String clientKey, RateLimitRule rule){
        return store.getAndIncrement(clientKey, rule, Instant.now(clock));
    }
}
