package com.ratelimiter.FluxWard.core.algorithm;

import com.ratelimiter.FluxWard.core.RateLimiter;
import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.store.RateLimitStore;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class TokenBucketRateLimiter implements RateLimiter {

    private final RateLimitStore store;
    private final Clock clock;

    public TokenBucketRateLimiter(RateLimitStore store, Clock clock) {
        this.store = store;
        this.clock = clock;
    }

    @Override
    public RateLimitResult tryAcquire(String clientKey, RateLimitRule rule) {
        return store.getAndIncrement(clientKey, rule, clock.instant());
    }
}
