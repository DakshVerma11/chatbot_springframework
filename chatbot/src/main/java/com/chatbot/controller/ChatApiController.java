package com.chatbot.controller;

import com.chatbot.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatApiController {

    private final ChatbotService chatbotService;

    public ChatApiController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> processMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        Map<String, String> responseMap = new HashMap<>();
        
        try {
            // Debug: Write to a file we can easily find
            try (PrintWriter writer = new PrintWriter(new FileWriter("c:/temp/chatbot_debug.log", true))) {
                writer.println("Received message: " + message);
                
                // Process the message
                String response = chatbotService.processMessage(message);
                writer.println("Response: " + response);
                
                responseMap.put("response", response);
                return ResponseEntity.ok(responseMap);
            } catch (Exception e) {
                // Log file write errors to console
                System.err.println("Failed to write to debug log: " + e.getMessage());
            }
        } catch (Exception e) {
            // Write exception to file
            try (PrintWriter writer = new PrintWriter(new FileWriter("c:/temp/chatbot_error.log", true))) {
                writer.println("ERROR PROCESSING: " + message);
                e.printStackTrace(writer);
            } catch (Exception ex) {
                System.err.println("Failed to write error log: " + ex.getMessage());
            }
            
            // Also to console
            System.err.println("ERROR PROCESSING MESSAGE: " + message);
            e.printStackTrace();
            
            responseMap.put("response", "I'm sorry, I encountered an error while processing your request. Please try again.");
            return ResponseEntity.status(500).body(responseMap);
        }
        
        // Fallback return if file writing failed but message processing succeeded
        responseMap.put("response", "Something went wrong with logging, but your message was processed.");
        return ResponseEntity.ok(responseMap);
    }
}