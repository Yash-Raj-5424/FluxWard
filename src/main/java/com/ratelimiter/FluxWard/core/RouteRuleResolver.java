package com.ratelimiter.FluxWard.core;

import com.ratelimiter.FluxWard.config.RateLimiterProperties;
import com.ratelimiter.FluxWard.config.RouteRuleProperties;
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
        List<RouteRuleProperties> routes = properties.getRoutes();
        if(routes == null || routes.isEmpty())  return defaultRule;

        for(RouteRuleProperties route: routes){
            if(pathMatcher.match(route.getPath(), reqPath)){
                return new RateLimitRule(
                        route.getCapacity()            != null ? route.getCapacity()            : defaultRule.getCapacity(),
                        route.getRefillRatePerSecond() != null ? route.getRefillRatePerSecond() : defaultRule.getRefillRatePerSecond(),
                        route.getWindowMs()            != null ? route.getWindowMs()            : defaultRule.getWindowMs(),
                        route.getKeyType()             != null ? route.getKeyType()             : defaultRule.getKeyType(),
                        route.getAlgorithm()           != null ? route.getAlgorithm()           : defaultRule.getAlgorithm()
                );
            }
        }

        return defaultRule;
    }
}
