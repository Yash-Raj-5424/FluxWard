package com.ratelimiter.FluxWard.service;

import com.ratelimiter.FluxWard.config.RateLimiterProperties;
import com.ratelimiter.FluxWard.core.AlgorithmFactory;
import com.ratelimiter.FluxWard.core.RateLimiter;
import com.ratelimiter.FluxWard.core.RouteRuleResolver;
import com.ratelimiter.FluxWard.metrics.RateLimitMetrics;
import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
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

    private final RedisRateLimitStore redisStore;
    private final AlgorithmFactory algorithmFactory;
    private final RouteRuleResolver routeResolver;
    private final InMemoryFallBackStore fallBackStore;
    private final RateLimiterProperties properties;
    private final RateLimitMetrics metrics;

    public RateLimitResult check(String clientKey, String requestPath) {
        RateLimitRule rule = routeResolver.resolve(requestPath);
        boolean redisUp = redisStore.isAvailable();
        RateLimitResult result;

        if (redisUp) {
            Timer timer = metrics.redisLatencyTimer(rule.getAlgorithm().name());
            result = timer.record(() ->
                    algorithmFactory.get(rule.getAlgorithm())
                            .tryAcquire(clientKey, rule)
            );
            if (result == null) {
                result = properties.isFailOpen()
                        ? fallBackStore.getAndIncrement(clientKey, rule, Instant.now())
                        : RateLimitResult.rejected(5000L, Instant.now().plusSeconds(5));

            }
        }else if (properties.isFailOpen()) {
                result = fallBackStore.getAndIncrement(clientKey, rule, Instant.now());
            } else {
                result = RateLimitResult.rejected(5_000L, Instant.now().plusSeconds(5));
            }

            if (result.isAllowed()) {
                metrics.recordAllowed(clientKey, rule, requestPath);
            } else {
                metrics.recordRejected(clientKey, rule, requestPath);
            }

            return result;
        }
}

