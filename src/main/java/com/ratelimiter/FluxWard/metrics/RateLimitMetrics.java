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
//        System.out.println("Recording ALLOWED metric for route: " + route);
        meterRegistry.counter("rate_limit_decisions",
                "result", "allowed",
                "algorithm", rule.getAlgorithm().name(),
                "route", route
        ).increment();
    }
    public void recordRejected(String clientKey, RateLimitRule rule, String route) {
        meterRegistry.counter("rate_limit_decisions",
                "result", "rejected",
                "algorithm", rule.getAlgorithm().name(),
                "route", route
        ).increment();
    }

    public Timer redisLatencyTimer(String algorithm) {
        return meterRegistry.timer("rate_limit_redis_latency",
                "algorithm", algorithm
        );
    }
}
