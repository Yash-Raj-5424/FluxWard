package com.ratelimiter.FluxWard.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "rate-limiter")
@Validated
@Getter
@Setter
@Component
public class RateLimiterProperties {

    @NotNull
    private Long capacity = 100L;
    @NotNull private Long refillRatePerSecond = 10L;
    @NotNull private Long windowMs = 60_000L;
    @NotNull private Boolean failOpen = true;
    @NotNull private String keyType = "API_KEY";

    private List<RouteRuleProperties> routes = new ArrayList<>();

    @Getter
    @Setter
    public static class RouteRuleProperties {
        @NotNull private String path;
        @NotNull private Algorithm algorithm;
        @NotNull private Long capacity;
        @NotNull private Long refillRatePerSecond;
        @NotNull private Long windowMs;
    }
}
