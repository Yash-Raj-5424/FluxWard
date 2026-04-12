package com.ratelimiter.FluxWard.store;

import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Primary
public class RedisRateLimitStore implements RateLimitStore{

    private final StringRedisTemplate redis;

    private static final String TOKEN_BUCKET_SCRIPT = """
            local key = KEYS[1]
        local capacity = tonumber(ARGV[1])
        local refillRate = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])

        local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')
        local tokens = tonumber(bucket[1]) or capacity
        local lastRefill = tonumber(bucket[2]) or now

        local elapsed = now - lastRefill
        local refillTokens = math.floor(elapsed * refillRate)
        tokens = math.min(tokens + refillTokens, capacity)
        lastRefill = now

        if tokens > 0 then
            tokens = tokens - 1
            redis.call('HMSET', key, 'tokens', tokens, 'last_refill', lastRefill)
            return {1, tokens} -- Allowed, return remaining tokens
        else
            redis.call('HMSET', key, 'tokens', tokens, 'last_refill', lastRefill)
            return {0, 0} -- Not allowed, no tokens left
        end
    """;

    public RedisRateLimitStore(StringRedisTemplate redis) {
        this.redis = redis;
    }


    public RateLimitResult getAndIncrement(String clientKey, RateLimitRule rule, Instant now) {
        List<Object> result = redis.execute(
                new DefaultRedisScript<>(TOKEN_BUCKET_SCRIPT, List.class),
                List.of("rl:" + clientKey),
                String.valueOf(rule.getCapacity()),
                String.valueOf(rule.getRefillRatePerSecond()),
                String.valueOf(now.toEpochMilli())
        );

        boolean allowed = ((Long) result.get(0)) == 1L;
        long remaining  = (Long) result.get(1);
        Instant resetAt = now.plusSeconds(1);

        return allowed
                ? RateLimitResult.allowed(remaining, rule.getCapacity(), resetAt)
                : RateLimitResult.rejected(1000L, resetAt);
    }

    @Override
    public boolean isAvailabe() {
        return false;
    }
}
