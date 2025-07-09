package com.chatbot.service;

import com.chatbot.model.ChatbotResponse;
import com.chatbot.repository.ChatbotResponseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ChatbotService {

    private final ChatbotResponseRepository responseRepository;
    private final Random random = new Random();

    public ChatbotService(ChatbotResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    public String processMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Please type something to start a conversation.";
        }

        String userMessage = message.toLowerCase().trim();
        System.out.println("Processing message: " + userMessage);
        
        // Get all responses from the database
        List<ChatbotResponse> allResponses = responseRepository.findAll();
        List<ChatbotResponse> matchingResponses = new ArrayList<>();
        
        // Go through each response in the database
        for (ChatbotResponse response : allResponses) {
            if (response.getKeywords() != null) {
                // Split the keywords by comma
                String[] keywords = response.getKeywords().split(",");
                
                // Check each keyword
                for (String keyword : keywords) {
                    keyword = keyword.toLowerCase().trim();
                    //System.out.println("Checking keyword: '" + keyword + "' in message: '" + userMessage + "'");
                    
                    // Check for exact keyword matches
                    if (userMessage.equals(keyword) ||              // Exact match
                        userMessage.contains(" " + keyword + " ") || // Keyword as a complete word in the middle
                        userMessage.startsWith(keyword + " ") ||     // Keyword at the start
                        userMessage.endsWith(" " + keyword) ||       // Keyword at the end
                        // Special case for multi-word keywords
                        (keyword.contains(" ") && userMessage.contains(keyword))) {
                        
                        System.out.println("Found match with keyword: " + keyword);
                        matchingResponses.add(response);
                        break; // Move to the next response once we find a match
                    }
                }
            }
        }
        
        System.out.println("Found " + matchingResponses.size() + " matching responses");
        
        // If no matching responses were found, return the default message
        if (matchingResponses.isEmpty()) {
            return "I'm sorry, I don't understand that. Could you try rephrasing?";
        }

        // Randomly select one of the matching responses
        ChatbotResponse selectedResponse = matchingResponses.get(random.nextInt(matchingResponses.size()));
        System.out.println("Selected response with keywords: " + selectedResponse.getKeywords());
        
        // If the response has a link, append it to the response text
        if (selectedResponse.hasLink()) {
            return selectedResponse.getResponse() + "\n\nClick here for more information: " + 
                   selectedResponse.getLinkText() + " (" + selectedResponse.getLinkUrl() + ")";
        }
        
        return selectedResponse.getResponse();
    }

    public ChatbotResponse addResponse(ChatbotResponse response) {
        return responseRepository.save(response);
    }

    public List<ChatbotResponse> getAllResponses() {
        return responseRepository.findAll();
    }
}