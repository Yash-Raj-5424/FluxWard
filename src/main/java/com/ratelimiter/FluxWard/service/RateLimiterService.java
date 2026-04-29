package com.ratelimiter.FluxWard.service;

import com.ratelimiter.FluxWard.config.RateLimiterProperties;
import com.ratelimiter.FluxWard.core.AlgorithmFactory;
import com.ratelimiter.FluxWard.core.RateLimiter;
import com.ratelimiter.FluxWard.core.RouteRuleResolver;
import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.store.InMemoryFallBackStore;
import com.ratelimiter.FluxWard.store.RateLimitStore;
import com.ratelimiter.FluxWard.store.RedisRateLimitStore;
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

    public RateLimitResult check(String clientKey, String requestPath){
        RateLimitRule rule = routeResolver.resolve(requestPath);
        boolean redisUp = redisStore.isAvailable();

        if(redisUp) {
            return algorithmFactory.get(rule.getAlgorithm())
                    .tryAcquire(clientKey, rule);
        }else if(properties.isFailOpen()){
            return fallBackStore.getAndIncrement(clientKey, rule, Instant.now());
        }else{
            return RateLimitResult.rejected(5_000L, Instant.now().plusSeconds(5));
        }
    }
}
