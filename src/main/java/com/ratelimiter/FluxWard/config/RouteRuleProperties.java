package com.ratelimiter.FluxWard.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RouteRuleProperties {

    @NotNull private String path;
    @NotNull private Algorithm algorithm;
    @NotNull private Long capacity;
    @NotNull private Long refillRatePerSecond;
    @NotNull private Long windowMs;
}
