package com.chatbot.controller;

import com.chatbot.model.ChatbotResponse;
import com.chatbot.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @ResponseBody
    @Transactional
    public ResponseEntity<ChatbotResponse> addResponse(@RequestBody ChatbotResponse response) {
        try {
            ChatbotResponse savedResponse = chatbotService.addResponse(response);
            return ResponseEntity.ok(savedResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @PutMapping("/api/response/{id}")
    @ResponseBody
    @Transactional
    public ResponseEntity<ChatbotResponse> updateResponse(
            @PathVariable("id") Long id,  // Add explicit name here
            @RequestBody ChatbotResponse response) {
        try {
            System.out.println("Updating response with ID: " + id);
            Optional<ChatbotResponse> existingResponse = chatbotService.getResponseById(id);
            if (existingResponse.isPresent()) {
                response.setId(id); // Ensure ID is set correctly
                ChatbotResponse updatedResponse = chatbotService.updateResponse(response);
                return ResponseEntity.ok(updatedResponse);
            } else {
                System.out.println("Response with ID " + id + " not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error updating response: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @DeleteMapping("/api/response/{id}")
    @ResponseBody
    @Transactional
    public ResponseEntity<String> deleteResponse(@PathVariable("id") Long id) {  // Add explicit name here
        try {
            System.out.println("Deleting response with ID: " + id);
            Optional<ChatbotResponse> existingResponse = chatbotService.getResponseById(id);
            if (existingResponse.isPresent()) {
                chatbotService.deleteResponse(id);
                return ResponseEntity.ok("Response deleted successfully");
            } else {
                System.out.println("Response with ID " + id + " not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error deleting response: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/api/responses")
    @ResponseBody
    public ResponseEntity<List<ChatbotResponse>> getAllResponses() {
        return ResponseEntity.ok(chatbotService.getAllResponses());
    }
    
    @GetMapping("/api/response/{id}")
    @ResponseBody
    public ResponseEntity<ChatbotResponse> getResponseById(@PathVariable("id") Long id) {
        Optional<ChatbotResponse> response = chatbotService.getResponseById(id);
        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}