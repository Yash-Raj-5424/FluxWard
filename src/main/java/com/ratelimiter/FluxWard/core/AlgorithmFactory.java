package com.ratelimiter.FluxWard.core;


import com.ratelimiter.FluxWard.config.Algorithm;
import com.ratelimiter.FluxWard.core.algorithm.FixedWindowRateLimiter;
import com.ratelimiter.FluxWard.core.algorithm.SlidingWindowRateLimiter;
import com.ratelimiter.FluxWard.core.algorithm.TokenBucketRateLimiter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AlgorithmFactory {

    private final TokenBucketRateLimiter tokenBucket;
    private final FixedWindowRateLimiter fixedWindow;
    private final SlidingWindowRateLimiter slidingWindow;


    public RateLimiter get(Algorithm algorithm){
        return switch(algorithm) {
            case TOKEN_BUCKET -> tokenBucket;
            case FIXED_WINDOW -> fixedWindow;
            case SLIDING_WINDOW -> slidingWindow;
        };
    }
}
