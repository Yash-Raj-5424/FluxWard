package com.ratelimiter.FluxWard.model;

import com.ratelimiter.FluxWard.config.Algorithm;
import com.ratelimiter.FluxWard.model.enums.KeyType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;


@Getter
@RequiredArgsConstructor
public class RateLimitRule {
    private final long capacity;
    private final long refillRatePerSecond;
    private final long windowMs;
    private final KeyType keyType;
    private final Algorithm algorithm;

    public enum KeyType {
        API_KEY,
        IP,
        JWT_SUBJECT
    }
}
