package com.alancortez.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/api/greeting")
    public Greeting greeting(@RequestParam(defaultValue = "World") String name) {
        return new Greeting("Hello, " + name + "!");
    }

    // Simple data class for JSON response
    record Greeting(String message) {}
}