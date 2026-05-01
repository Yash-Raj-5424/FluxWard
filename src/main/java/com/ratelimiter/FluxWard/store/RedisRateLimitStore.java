package com.ratelimiter.FluxWard.store;

import com.ratelimiter.FluxWard.config.Algorithm;
import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.store.script.FixedWindowScript;
import com.ratelimiter.FluxWard.store.script.SlidingWindowScript;
import com.ratelimiter.FluxWard.store.script.TokenBucketScript;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class RedisRateLimitStore implements RateLimitStore{

    private final StringRedisTemplate redis;
    private final TokenBucketScript tokenBucketScript;
    private final FixedWindowScript fixedWindowScript;
    private final SlidingWindowScript slidingWindowScript;

    @Override
    @SuppressWarnings("unchecked")
    public RateLimitResult getAndIncrement(String clientKey, RateLimitRule rule, Instant now) {

        long nowMs = now.toEpochMilli();
        List<Long> result;
        Instant resetAt;

        return switch (rule.getAlgorithm()) {
            case FIXED_WINDOW -> {
                result = (List<Long>) redis.execute(
                        fixedWindowScript,
                        List.of("rl:" + clientKey),
                        String.valueOf(rule.getCapacity()),
                        String.valueOf(rule.getWindowMs()),
                        String.valueOf(nowMs)
                );
                System.out.println("FIXED WINDOW result: " + result);
                if (result == null || result.isEmpty()) {
                    yield RateLimitResult.allowed(rule.getCapacity(), rule.getCapacity(),
                            now.plusMillis(rule.getWindowMs()));
                }
                long ttlMs = result.get(2);
                resetAt = now.plusMillis(ttlMs > 0 ? ttlMs : rule.getWindowMs());
                yield result.get(0) == 1L
                        ? RateLimitResult.allowed(result.get(1), rule.getCapacity(), resetAt)
                        : RateLimitResult.rejected(ttlMs, resetAt);
            }

            case SLIDING_WINDOW -> {
                long bucket = nowMs / rule.getWindowMs();
                String curKey = "rl:" + clientKey + ":sw:" + bucket;
                String prevKey = "rl:" + clientKey + ":sw:" + (bucket - 1);
                result = (List<Long>) redis.execute(
                        slidingWindowScript,
                        List.of(curKey, prevKey),
                        String.valueOf(rule.getCapacity()),
                        String.valueOf(rule.getWindowMs()),
                        String.valueOf(nowMs)
                );
                System.out.println("SLIDING WINDOW result: " + result);
                if (result == null || result.isEmpty()) {
                    yield RateLimitResult.allowed(rule.getCapacity(), rule.getCapacity(),
                            now.plusMillis(rule.getWindowMs()));
                }
                long ttlMs = result.get(2);
                resetAt = now.plusMillis(ttlMs > 0 ? ttlMs : rule.getWindowMs());
                yield result.get(0) == 1L
                        ? RateLimitResult.allowed(result.get(1), rule.getCapacity(), resetAt)
                        : RateLimitResult.rejected(ttlMs, resetAt);
            }

            case TOKEN_BUCKET -> {
                result = (List<Long>) redis.execute(
                        tokenBucketScript,
                        List.of("rl:" + clientKey),
                        String.valueOf(rule.getCapacity()),
                        String.valueOf(rule.getRefillRatePerSecond()),
                        String.valueOf(now.getEpochSecond())
                );
                System.out.println("TOKEN BUCKET result: " + result);
                if (result == null || result.isEmpty()) {
                    yield RateLimitResult.allowed(rule.getCapacity(), rule.getCapacity(),
                            now.plusSeconds(1));
                }
                resetAt = now.plusSeconds(1);
                yield result.get(0) == 1L
                        ? RateLimitResult.allowed(result.get(1), rule.getCapacity(), resetAt)
                        : RateLimitResult.rejected(1000L, resetAt);
            }
        };
    }

    @Override
    public boolean isAvailable() {
        try {
            redis.opsForValue().get("health-check");
//            System.out.println("Redis is AVAILABLE");
            return true;
        } catch (Exception e) {
//            System.out.println("Redis is UNAVAILABLE: " + e.getMessage());
            return false;
        }
    }
}
