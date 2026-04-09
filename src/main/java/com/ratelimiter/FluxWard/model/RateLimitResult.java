package com.ratelimiter.FluxWard.model;

import lombok.Value;

import java.time.Instant;

@Value
public class RateLimitResult {

    boolean allowed;
    long limit;
    long remaining;
    Instant resetAt;
    long retryAfterMs;

    public static RateLimitResult allowed(long remaining, long limit, Instant resetAt){
        // todo
        return null;
    }

    public static RateLimitResult rejected(long retryAfterMs, Instant resetAt){
        // todo
        return null;
    }

}
