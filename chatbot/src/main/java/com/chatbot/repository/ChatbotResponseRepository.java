package com.chatbot.repository;

import com.chatbot.model.ChatbotResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotResponseRepository extends JpaRepository<ChatbotResponse, Long> {
    
    // Simple implementation using LIKE operator on comma-separated keywords
    @Query("SELECT c FROM ChatbotResponse c WHERE " +
           "LOWER(:message) LIKE CONCAT('%', LOWER(c.keywords), '%') OR " + 
           "LOWER(c.keywords) LIKE CONCAT('%', LOWER(:message), '%')")
    List<ChatbotResponse> findMatchingResponses(@Param("message") String message);
    
    // Alternative method using native SQL if needed
    @Query(nativeQuery = true, value = 
           "SELECT * FROM chatbot_data WHERE " +
           "LOWER(?1) LIKE CONCAT('%', LOWER(keywords), '%') OR " +
           "LOWER(keywords) LIKE CONCAT('%', LOWER(?1), '%')")
    List<ChatbotResponse> findMatchingResponsesNative(String message);
}