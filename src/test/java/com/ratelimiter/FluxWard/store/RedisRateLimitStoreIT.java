package com.ratelimiter.FluxWard.store;


import com.ratelimiter.FluxWard.model.RateLimitResult;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import com.ratelimiter.FluxWard.model.enums.KeyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

@Testcontainers
public class RedisRateLimitStoreIT {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(
            DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    private RedisRateLimitStore store;
    private RateLimitRule rule;

    @BeforeEach
    void setUp() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                redis.getHost(), redis.getMappedPort(6739));

        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.afterPropertiesSet();

        StringRedisTemplate template = new StringRedisTemplate(factory);
        template.afterPropertiesSet();

        store = new RedisRateLimitStore(template);
        rule = new RateLimitRule(5, 1, 60_000, KeyType.API_KEY);

    }

    @Test
    void allow_requests_within_limit(){
        Instant now = Instant.now();
        for(int i=0; i<5; i++){
            assertTrue(store.getAndIncrement("test-key", rule, now).isAllowed());
        }
    }

    @Test
    void reject_requests_beyond_limit(){
        Instant now = Instant.now();
        for(int i=0; i<5; i++){
            store.getAndIncrement("test-key-2", rule, now);
        }
        RateLimitResult sixth = store.getAndIncrement("test-key-2", rule, now);
        assertFalse(sixth.isAllowed());
    }

    @Test
    void lua_script_atomic_under_concurrent_loads() throws InterruptedException {
        int threads = 20;
        int capacity = 5;

        RateLimitRule concurrentRule = new RateLimitRule(capacity, 1, 60_000, KeyType.API_KEY);
        Instant now = Instant.now();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger allowedCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(threads);
        List<Future<?>> futures = new ArrayList<>();

        for(int i = 0; i < threads; i++){
            futures.add(executor.submit(() -> {
                try {
                    RateLimitResult res = store.getAndIncrement("concurrent-key", concurrentRule, now);
                    if (res.isAllowed())
                        allowedCount.incrementAndGet();
                    else
                        rejectedCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(capacity, allowedCount.get());
        assertEquals(threads - capacity, rejectedCount.get());
    }

    @Test
    void redisAvailability() {
        assertTrue(store.isAvailable());
    }
}
