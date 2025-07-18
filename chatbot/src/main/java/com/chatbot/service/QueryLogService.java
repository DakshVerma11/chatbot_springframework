package com.chatbot.service;

import com.chatbot.model.QueryLog;
import com.chatbot.repository.QueryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QueryLogService {
    
    private final QueryLogRepository queryLogRepository;
    
    @Autowired
    public QueryLogService(QueryLogRepository queryLogRepository) {
        this.queryLogRepository = queryLogRepository;
    }

    public void logQuery(QueryLog log) {
        queryLogRepository.save(log);
    }
    /**
     * Get all query logs
     */
    public List<QueryLog> getAllQueryLogs() {
        return queryLogRepository.findAll();
    }

    /**
     * Get query log by id
     */
    public Optional<QueryLog> getQueryLogById(Long id) {
        return queryLogRepository.findById(id);
    }

    /**
     * Get query logs by user ID
     */
    public List<QueryLog> getQueryLogsByUserId(String userId) {
        return queryLogRepository.findByUserId(userId);
    }

    /**
     * Get query logs by IP address
     */
    public List<QueryLog> getQueryLogsByIpAddress(String ipAddress) {
        return queryLogRepository.findByIpAddress(ipAddress);
    }

    /**
     * Search query logs by content
     */
    public List<QueryLog> searchQueryLogs(String queryText) {
        return queryLogRepository.findByQueryContainingIgnoreCase(queryText);
    }
}