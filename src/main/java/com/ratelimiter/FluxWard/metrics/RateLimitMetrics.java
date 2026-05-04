package com.ratelimiter.FluxWard.metrics;

import com.ratelimiter.FluxWard.model.RateLimitRule;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RateLimitMetrics {

    private final MeterRegistry meterRegistry;

    public void recordAllowed(String clientKey, RateLimitRule rule, String route){
        Counter.builder("rate_limit_decisions")
                .tag("result", "allowed")
                .tag("algorithm", rule.getAlgorithm().name())
                .tag("route", route)
                .register(meterRegistry)
                .increment();
    }
    public void recordRejected(String clientKey, RateLimitRule rule, String route) {
        Counter.builder("rate_limit_decisions")
                .tag("result", "rejected")
                .tag("algorithm", rule.getAlgorithm().name())
                .tag("route", route)
                .register(meterRegistry)
                .increment();
    }

    public Timer redisLatencyTimer(String algorithm) {
        return Timer.builder("rate_limit_redis_latency")
                .tag("algorithm", algorithm)
                .register(meterRegistry);
    }
}
