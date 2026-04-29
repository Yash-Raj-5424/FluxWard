package com.ratelimiter.FluxWard.core;

import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;

public interface RateLimiter {

    RateLimitResult tryAcquire(String clientKey, RateLimitRule rule);
}

