package com.ratelimiter.FluxWard.store.script;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenBucketScript extends DefaultRedisScript<List> {

    public TokenBucketScript(){
        setResultType(List.class);
        setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/token_bucket.lua")));
    }

}
