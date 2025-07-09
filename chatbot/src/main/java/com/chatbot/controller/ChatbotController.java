package com.chatbot.controller;

import com.chatbot.model.ChatbotResponse;
import com.chatbot.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatbotController {

    private final ChatbotService chatbotService;

    // Manual constructor for dependency injection
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("responses", chatbotService.getAllResponses());
        model.addAttribute("newResponse", new ChatbotResponse());
        return "admin";
    }

    @PostMapping("/api/response")
    public ResponseEntity<ChatbotResponse> addResponse(@RequestBody ChatbotResponse response) {
        return ResponseEntity.ok(chatbotService.addResponse(response));
    }

    @GetMapping("/api/responses")
    public ResponseEntity<List<ChatbotResponse>> getAllResponses() {
        return ResponseEntity.ok(chatbotService.getAllResponses());
    }
}