package com.ratelimiter.FluxWard.store;

import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;

import java.time.Instant;

public interface RateLimitStore  {

    RateLimitResult getAndIncrement(String clientKey, RateLimitRule rule, Instant now);
    boolean isAvailable();

}
