package com.ratelimiter.FluxWard.config;

import com.ratelimiter.FluxWard.model.RateLimitRule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RouteRuleProperties {

    private String path;
    private Algorithm algorithm;
    private Long capacity;
    private Long refillRatePerSecond;
    private Long windowMs;
    private RateLimitRule.KeyType keyType;
}
