package com.chatbot.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeywordMatcher {
    
    /**
     * Checks if any of the keywords exactly match the words in the message.
     * 
     * @param message The user message
     * @param keywordsString Comma-separated keywords
     * @return true if there's a match, false otherwise
     */
    public static boolean matchesKeywords(String message, String keywordsString) {
        if (message == null || keywordsString == null) {
            return false;
        }
        
        // Normalize message and split into words
        String normalizedMessage = message.toLowerCase().trim();
        @SuppressWarnings("unused")
		Set<String> messageWords = new HashSet<>(Arrays.asList(normalizedMessage.split("\\s+")));
        
        // Get all keywords
        String[] keywords = keywordsString.split(",");
        
        // Check each keyword
        for (String keyword : keywords) {
            keyword = keyword.toLowerCase().trim();
            
            // Check if the message contains the exact keyword phrase
            if (normalizedMessage.contains(keyword)) {
                // For multi-word keywords, ensure they appear exactly as specified
                if (keyword.contains(" ")) {
                    return true; // Multi-word keyword match is definitive
                }
                
                // For single-word keywords, check if it's a standalone word (not part of another word)
                String[] messageParts = normalizedMessage.split("\\s+");
                for (String part : messageParts) {
                    if (part.equals(keyword)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}