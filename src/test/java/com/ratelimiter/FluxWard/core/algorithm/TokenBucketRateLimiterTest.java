package com.ratelimiter.FluxWard.core.algorithm;

import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.model.enums.KeyType;
import com.ratelimiter.FluxWard.store.InMemoryFallBackStore;
import com.ratelimiter.FluxWard.store.RateLimitStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TokenBucketRateLimiterTest {

    private RateLimitStore store;
    private RateLimitRule rule;

    @BeforeEach
    void setUp(){
        store = new InMemoryFallBackStore();
        rule = new RateLimitRule(3, 1, 60_000, KeyType.API_KEY);
    }

    @Test
    void allow_requests_within_capacity(){
        Instant now = Instant.now();
        RateLimitResult res1 = store.getAndIncrement("client1", rule, now);
        RateLimitResult res2 = store.getAndIncrement("client1", rule, now);
        RateLimitResult res3 = store.getAndIncrement("client1", rule, now);

        assertTrue(res1.isAllowed());
        assertTrue(res2.isAllowed());
        assertTrue(res3.isAllowed());
    }

    @Test
    void reject_requests_exceeding_capacity(){
        Instant now = Instant.now();

        store.getAndIncrement("client2", rule, now);
        store.getAndIncrement("client2", rule, now);
        store.getAndIncrement("client2", rule, now);

        RateLimitResult res4 = store.getAndIncrement("client2", rule, now);
        assertTrue(!res4.isAllowed());
    }

     @Test
    void remaining_decrements_correctly(){
        Instant now = Instant.now();

        RateLimitResult res1 = store.getAndIncrement("client3", rule, now);
        RateLimitResult res2 = store.getAndIncrement("client3", rule, now);

        assertEquals(2,res1.getRemaining() == 1);
        assertEquals(1,res2.getRemaining() == 0);
    }

    @Test
    void different_clients_have_separate_buckets(){
        Instant now = Instant.now();

        RateLimitResult res1 = store.getAndIncrement("clientA", rule, now);
        RateLimitResult res2 = store.getAndIncrement("clientB", rule, now);
        RateLimitResult res3 = store.getAndIncrement("clientA", rule, now);

        RateLimitResult clientAFourth = store.getAndIncrement("clientB", rule, now);
        RateLimitResult clientBFirst = store.getAndIncrement("client-B", rule, now);

        assertFalse(clientAFourth.isAllowed());
        assertTrue(clientBFirst.isAllowed());
    }

    @Test
    void rejected_result_contains_retry_after(){
        Instant now = Instant.now();

        store.getAndIncrement("clientX", rule, now);
        store.getAndIncrement("clientX", rule, now);
        store.getAndIncrement("clientX", rule, now);

        RateLimitResult rejected = store.getAndIncrement("clientX", rule, now);

        assertFalse(rejected.isAllowed());
        assertTrue(rejected.getRetryAfterMs() > 0);
    }

    @Test
    void allowed_result_has_correct_limit(){
        Instant now = Instant.now();

        RateLimitResult res = store.getAndIncrement("clientY", rule, now);

        assertTrue(res.isAllowed());
        assertEquals(3, res.getLimit());
    }
}
