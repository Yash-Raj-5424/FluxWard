package com.ratelimiter.FluxWard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "ok";
    }

    @GetMapping("/test/fixedwindow")
    public String testFixedWindow(){
        return "fixed_window ok";
    }

    @GetMapping("/test/slidingwindow")
    public String testSlidingWindow(){
        return "sliding_window ok";
    }
}
