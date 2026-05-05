package com.ratelimiter.FluxWard.store;

import com.ratelimiter.FluxWard.config.RateLimiterProperties;
import com.ratelimiter.FluxWard.core.AlgorithmFactory;
import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CircuitBreakerRedisStore {

    private final AlgorithmFactory algorithmFactory;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public RateLimitResult execute(String clientKey, RateLimitRule rule, Instant now) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("redis");
        return CircuitBreaker.decorateSupplier(circuitBreaker,
                () -> algorithmFactory.get(rule.getAlgorithm())
                        .tryAcquire(clientKey, rule)
        ).get();
    }

    public boolean isRedisAvailable() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("redis");
        return cb.getState() == CircuitBreaker.State.CLOSED
                || cb.getState() == CircuitBreaker.State.HALF_OPEN;
    }
}
