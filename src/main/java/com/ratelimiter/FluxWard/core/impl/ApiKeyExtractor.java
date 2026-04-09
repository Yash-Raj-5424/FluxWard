package com.ratelimiter.FluxWard.core.impl;

import com.ratelimiter.FluxWard.core.ClientKeyExtractor;
import com.ratelimiter.FluxWard.exception.MissingApiKeyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Primary
public class ApiKeyExtractor implements ClientKeyExtractor {

    public String extract(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader("X-API-Key"))
                .orElseThrow(() -> new MissingApiKeyException());
    }
}
