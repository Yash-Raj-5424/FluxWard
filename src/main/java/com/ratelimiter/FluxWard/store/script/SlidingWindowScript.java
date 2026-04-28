package com.ratelimiter.FluxWard.store.script;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;


import java.util.List;

public class SlidingWindowScript extends DefaultRedisScript<List> {

    public SlidingWindowScript(){
        setResultType(List.class);
        setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/sliding_window.lua")));
    }
}
