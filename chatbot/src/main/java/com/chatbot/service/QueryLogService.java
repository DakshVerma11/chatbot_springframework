package com.chatbot.service;

import com.chatbot.model.QueryLog;
import com.chatbot.repository.QueryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}