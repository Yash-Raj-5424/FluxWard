package com.ratelimiter.FluxWard.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "rate-limiter")
@Validated
@Getter
@Setter
public class RateLimiterProperties {

    @NotNull
    private Long capacity = 100L;
    @NotNull private Long refillRatePerSecond = 10L;
    @NotNull private Long windowMs = 60_000L;
    @NotNull private Boolean failOpen = true;
    @NotNull private String keyType = "API_KEY";
}
