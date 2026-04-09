package com.ratelimiter.FluxWard.exception;

public class MissingApiKeyException extends RuntimeException {

    public MissingApiKeyException() {
        super("API key is missing in the request");
    }

}
