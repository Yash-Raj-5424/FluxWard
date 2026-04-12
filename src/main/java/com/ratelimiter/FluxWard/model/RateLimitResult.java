package com.ratelimiter.FluxWard.model;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.Instant;

@Getter
public class RateLimitResult {

    boolean allowed;
    long limit;
    long remaining;
    Instant resetAt;
    long retryAfterMs;

    public RateLimitResult(boolean b, long limit, long remaining, Instant resetAt, long l) {
        this.allowed = b;
        this.limit = limit;
        this.remaining = remaining;
        this.resetAt = resetAt;
        this.retryAfterMs = l;
    }

    public static RateLimitResult allowed(long remaining, long limit, Instant resetAt){
        return new RateLimitResult(true, limit, remaining, resetAt, 0L);
    }

    public static RateLimitResult rejected(long retryAfterMs, Instant resetAt){
        return new RateLimitResult(false, 0L, 0L, resetAt, retryAfterMs);
    }

}
