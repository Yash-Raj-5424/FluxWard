package com.ratelimiter.FluxWard.model;

import com.ratelimiter.FluxWard.config.Algorithm;
import com.ratelimiter.FluxWard.model.enums.KeyType;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
public class RateLimitRule {
    long capacity;
    long refillRatePerSecond;
    long windowMs;
    KeyType keyType;
    private final Algorithm algorithm;

     public RateLimitRule(long capacity, long refillRatePerSecond, long windowMs, KeyType keyType, Algorithm algorithm) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.windowMs = windowMs;
        this.keyType = keyType;
        this.algorithm = algorithm;
    }

    public enum KeyType {
        API_KEY,
        IP,
        JWT_SUBJECT
    }
}
