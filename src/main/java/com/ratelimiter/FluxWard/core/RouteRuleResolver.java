package com.ratelimiter.FluxWard.core;

import com.ratelimiter.FluxWard.config.RateLimiterProperties;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@RequiredArgsConstructor
public class RouteRuleResolver {

    private final RateLimiterProperties properties;
    private final RateLimitRule defaultRule;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RateLimitRule resolve(String reqPath) {
        List<RateLimiterProperties.RouteRuleProperties> routes = properties.getRoutes();
        if(routes == null || routes.isEmpty())  return defaultRule;

        for(RateLimiterProperties.RouteRuleProperties route: routes){
            System.out.println("Matching: " + route.getPath() + " against: " + reqPath);
            if(pathMatcher.match(route.getPath(), reqPath)){
                return new RateLimitRule(
                        route.getCapacity() != null ? route.getCapacity() : defaultRule.getCapacity(),
                        route.getRefillRatePerSecond()  != null ? route.getRefillRatePerSecond() : defaultRule.getRefillRatePerSecond(),
                        route.getWindowMs() != null ? route.getWindowMs() : defaultRule.getWindowMs(),
                        defaultRule.getKeyType(),
                        route.getAlgorithm() != null ? route.getAlgorithm() : defaultRule.getAlgorithm()
                );
            }
        }

        return defaultRule;
    }
}
