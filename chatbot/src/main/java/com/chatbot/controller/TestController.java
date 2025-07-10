package com.chatbot.controller;

import com.chatbot.model.ChatbotResponse;
import com.chatbot.repository.ChatbotResponseRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    private final ChatbotResponseRepository repository;

    public TestController(ChatbotResponseRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/db")
    public String testDatabase() {
        try {
            long count = repository.count();
            List<ChatbotResponse> responses = repository.findAll();
            
            StringBuilder result = new StringBuilder();
            result.append("Database connection successful!\n");
            result.append("Total records: ").append(count).append("\n\n");
            
            if (responses.isEmpty()) {
                result.append("No records found in database!");
            } else {
                result.append("First 3 records:\n");
                int i = 0;
                for (ChatbotResponse response : responses) {
                    if (i++ >= 3) break;
                    result.append("ID: ").append(response.getId())
                          .append(", Keywords: ").append(response.getKeywords())
                          .append(", Response: ").append(response.getResponse().substring(0, Math.min(50, response.getResponse().length())))
                          .append("...\n");
                }
            }
            
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage() + "\n" + e.getClass().getName();
        }
    }
}