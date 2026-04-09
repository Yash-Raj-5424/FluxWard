package com.ratelimiter.FluxWard.core;

import jakarta.servlet.http.HttpServletRequest;

public interface ClientKeyExtractor  {

    String extract(HttpServletRequest request);
}
