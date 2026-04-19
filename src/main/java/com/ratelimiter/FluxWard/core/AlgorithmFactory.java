package com.ratelimiter.FluxWard.core;


import com.ratelimiter.FluxWard.config.Algorithm;
import com.ratelimiter.FluxWard.core.algorithm.FixedWindowRateLimiter;
import com.ratelimiter.FluxWard.core.algorithm.SlidingWindowRateLimiter;
import com.ratelimiter.FluxWard.core.algorithm.TokenBucketRateLimiter;

public class AlgorithmFactory {

    private final TokenBucketRateLimiter tokenBucket;
    private final FixedWindowRateLimiter fixedWindow;
    private final SlidingWindowRateLimiter slidingWindow;

    public AlgorithmFactory(TokenBucketRateLimiter tokenBucket, FixedWindowRateLimiter fixedWindow, SlidingWindowRateLimiter slidingWindow) {
        this.tokenBucket = tokenBucket;
        this.fixedWindow = fixedWindow;
        this.slidingWindow = slidingWindow;
    }

    public RateLimiter get(Algorithm algorithm){
        return switch(algorithm) {
            case TOKEN_BUCKET -> tokenBucket;
            case FIXED_WINDOW -> fixedWindow;
            case SLIDING_WINDOW -> slidingWindow;
        };
    }
}
