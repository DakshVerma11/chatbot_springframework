package com.chatbot.repository;

import com.chatbot.model.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {
    
    // Find logs by userId
    List<QueryLog> findByUserId(String userId);
    
    // Find logs by ipAddress
    List<QueryLog> findByIpAddress(String ipAddress);
    
    // Find logs containing specific query text
    List<QueryLog> findByQueryContainingIgnoreCase(String queryText);
    
    // Find logs between timestamps
    List<QueryLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    // Find recent logs (last 24 hours)
    default List<QueryLog> findRecentLogs() {
        return findByTimestampBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now());
    }
}