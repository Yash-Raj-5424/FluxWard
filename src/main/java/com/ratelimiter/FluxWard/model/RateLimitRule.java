package com.ratelimiter.FluxWard.model;

import com.ratelimiter.FluxWard.model.enums.KeyType;
import lombok.Value;

@Value
public class RateLimitRule {
    long capacity;
    long refillRatePerSecond;
    long windowMs;
    KeyType keyType;
}
