package com.ratelimiter.FluxWard.store;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryFallBackStore implements RateLimitStore{

    private final Cache<String, AtomicLong> cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    public RateLimitResult getAndIncrement(String key, RateLimitRule rule, Instant now) {
        AtomicLong counter = cache.get(key, k -> new AtomicLong(0));
        long count = counter.incrementAndGet();
        boolean isAllowed = count <= rule.getCapacity();
        return isAllowed
                ? RateLimitResult.allowed(rule.getCapacity() - count, rule.getCapacity(), now.plusSeconds(60))
                : RateLimitResult.rejected(60_000L, now.plusSeconds(60));
    }

    @Override
    public boolean isAvailable() {
        return true;    // this store is always available as it's in-memory
    }
}
