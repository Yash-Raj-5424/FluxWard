package com.ratelimiter.FluxWard.core;

import com.ratelimiter.FluxWard.core.impl.ApiKeyExtractor;
import com.ratelimiter.FluxWard.core.impl.IpExtractor;
import com.ratelimiter.FluxWard.core.impl.JwtKeyExtractor;
import com.ratelimiter.FluxWard.model.RateLimitRule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeyExtractorResolver {

    private final ApiKeyExtractor apiKeyExtractor;
    private final IpExtractor ipExtractor;
    private final JwtKeyExtractor jwtKeyExtractor;

    public String extract(HttpServletRequest request, RateLimitRule rule) {
        return switch (rule.getKeyType()) {
            case API_KEY     -> apiKeyExtractor.extract(request);
            case IP          -> ipExtractor.extract(request);
            case JWT_SUBJECT -> jwtKeyExtractor.extract(request);
        };
    }
}
