package com.ratelimiter.FluxWard.core.impl;

import com.ratelimiter.FluxWard.core.ClientKeyExtractor;
import jakarta.servlet.http.HttpServletRequest;

public class IpExtractor implements ClientKeyExtractor {

    @Override
    public String extract(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
