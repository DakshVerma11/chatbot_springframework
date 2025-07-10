package com.chatbot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Ping successful!");
        return response;
    }
    
    @PostMapping("/echo")
    public Map<String, String> echo(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("receivedMessage", request.get("message"));
        response.put("response", "You said: " + request.get("message"));
        return response;
    }
}