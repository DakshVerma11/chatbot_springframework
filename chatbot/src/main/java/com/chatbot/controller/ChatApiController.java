package com.chatbot.controller;

import com.chatbot.model.ChatbotResponse;
import com.chatbot.repository.ChatbotResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.chatbot.model.QueryLog;
import com.chatbot.service.QueryLogService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatApiController {

    private final ChatbotResponseRepository repository;
    private final QueryLogService queryLogService;
    private final Random random = new Random();

    @Autowired
    public ChatApiController(ChatbotResponseRepository repository, QueryLogService queryLogService) {
        this.repository = repository;
        this.queryLogService = queryLogService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> processMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        Map<String, String> responseMap = new HashMap<>();
        
        try {
            // Log receipt of message
            //System.out.println("RECEIVED MESSAGE: " + message);
            
            if (message == null || message.isEmpty()) {
                responseMap.put("response", "Please type a message.");
                return ResponseEntity.ok(responseMap);
            }
            
            // Normalize the user input
            String userInput = message.toLowerCase().trim();
            //System.out.println("Normalized user input: " + userInput);
            
            String ipAddress = "192.168.1.1";//assign actual ip address
            String userId = "user"; // assign "user" for now
            LocalDateTime timestamp = LocalDateTime.now();

            QueryLog log = new QueryLog(userId, ipAddress, userInput, timestamp);
            queryLogService.logQuery(log);
            
            // Get all responses from the database
            List<ChatbotResponse> allResponses = repository.findAll();
            //System.out.println("Found " + allResponses.size() + " total responses");
            
            // List to store matching responses
            List<ChatbotResponse> matchingResponses = new ArrayList<>();
            
            // Check each response for keyword matches
            for (ChatbotResponse response : allResponses) {
                if (response.getKeywords() != null && !response.getKeywords().isEmpty()) {
                    String keywordsString = response.getKeywords().toLowerCase();
                    //System.out.println("Checking response ID " + response.getId() + " with keywords: " + keywordsString);
                    
                    // Split the keywords string by comma
                    String[] keywords = keywordsString.split(",");
                    
                    // Check each keyword
                    for (String keyword : keywords) {
                        keyword = keyword.trim();
                        //System.out.println("  Checking if '" + userInput + "' contains keyword '" + keyword + "'");
                        
                        // Check for keyword match
                        if (keyword.length() > 0 && userInput.contains(keyword)) {
                            System.out.println("  MATCH FOUND with keyword: " + keyword);
                            matchingResponses.add(response);
                            break; // Once we find a match, move to the next response
                        }
                    }
                }
            }
            
            //System.out.println("Found " + matchingResponses.size() + " matching responses");
            
            // Return appropriate response
            if (matchingResponses.isEmpty()) {
                // No matching responses found
                System.out.println("No matches found, returning default message");
                responseMap.put("response", "I'm sorry, I don't understand that. Could you try rephrasing?");
            } else {
                // Select a random matching response
                ChatbotResponse selectedResponse = matchingResponses.get(random.nextInt(matchingResponses.size()));
                System.out.println("Selected response ID: " + selectedResponse.getId());
                
                // Format the response with link if available
                String responseText = selectedResponse.getResponse();
                
                if (selectedResponse.hasLink()) {
                    System.out.println("Response has link: " + selectedResponse.getLinkText() + " - " + selectedResponse.getLinkUrl());
                    responseText += "\n\nClick here for more information: " + 
                                   selectedResponse.getLinkText() + " (" + 
                                   selectedResponse.getLinkUrl() + ")";
                }
                
                responseMap.put("response", responseText);
            }
            
            return ResponseEntity.ok(responseMap);
            
        } catch (Exception e) {
            System.err.println("ERROR IN API: " + e.getMessage());
            e.printStackTrace();
            
            responseMap.put("response", "I'm sorry, I encountered an error: " + e.getMessage());
            return ResponseEntity.ok(responseMap);
        }
    }
}