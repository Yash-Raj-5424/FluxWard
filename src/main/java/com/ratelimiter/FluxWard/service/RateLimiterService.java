package com.ratelimiter.FluxWard.service;

import com.ratelimiter.FluxWard.config.RateLimiterProperties;
import com.ratelimiter.FluxWard.core.AlgorithmFactory;
import com.ratelimiter.FluxWard.core.RateLimiter;
import com.ratelimiter.FluxWard.core.RouteRuleResolver;
import com.ratelimiter.FluxWard.metrics.RateLimitMetrics;
import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.store.CircuitBreakerRedisStore;
import com.ratelimiter.FluxWard.store.InMemoryFallBackStore;
import com.ratelimiter.FluxWard.store.RateLimitStore;
import com.ratelimiter.FluxWard.store.RedisRateLimitStore;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final CircuitBreakerRedisStore circuitBreakerRedisStore;
    private final RouteRuleResolver routeResolver;
    private final InMemoryFallBackStore fallBackStore;
    private final RateLimiterProperties properties;
    private final RateLimitMetrics metrics;

    public RateLimitResult check(String clientKey, String requestPath) {
        RateLimitRule rule = routeResolver.resolve(requestPath);
        RateLimitResult result;

        try {
            Timer timer = metrics.redisLatencyTimer(rule.getAlgorithm().name());
            result = timer.record(() ->
                    circuitBreakerRedisStore.execute(clientKey, rule, Instant.now())
            );
            if (result == null)     result = fallback(clientKey, rule);

        } catch (Exception e) {
            result = fallback(clientKey, rule);
        }

        if (result.isAllowed())     metrics.recordAllowed(clientKey, rule, requestPath);
        else   metrics.recordRejected(clientKey, rule, requestPath);

        return result;
    }

    private RateLimitResult fallback(String clientKey, RateLimitRule rule) {
        if (properties.isFailOpen()) {
            return fallBackStore.getAndIncrement(clientKey, rule, Instant.now());
        }
        return RateLimitResult.rejected(5000L, Instant.now().plusSeconds(5));
    }
}

