package com.chatbot.controller;

import com.chatbot.model.QueryLog;
import com.chatbot.service.QueryLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/querylogs")
public class QueryLogController {

    private final QueryLogService queryLogService;

    @Autowired
    public QueryLogController(QueryLogService queryLogService) {
        this.queryLogService = queryLogService;
    }

    /**
     * Display all query logs in the HTML view
     */
    @GetMapping
    public String getAllQueryLogs(Model model) {
        List<QueryLog> querylogs = queryLogService.getAllQueryLogs();
        
        // Calculate statistics for dashboard
        Set<String> uniqueUserSet = querylogs.stream()
                .map(QueryLog::getUserId)
                .collect(Collectors.toSet());
        
        Set<String> uniqueIpSet = querylogs.stream()
                .map(QueryLog::getIpAddress)
                .collect(Collectors.toSet());
        
        model.addAttribute("querylogs", querylogs);
        model.addAttribute("uniqueUsers", uniqueUserSet.size());
        model.addAttribute("uniqueIps", uniqueIpSet.size());
        
        return "querylog";
    }

    /**
     * API endpoint to get a specific query log by ID
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public QueryLog getQueryLogById(@PathVariable Long id) {
        Optional<QueryLog> queryLog = queryLogService.getQueryLogById(id);
        return queryLog.orElse(null);
    }

    /**
     * Search and filter query logs
     * Fixed with explicit @RequestParam names
     */
    @GetMapping("/search")
    public String searchQueryLogs(
            @RequestParam(name = "queryText", required = false) String queryText,
            @RequestParam(name = "userIdParam", required = false) String userIdParam,
            @RequestParam(name = "ipAddressParam", required = false) String ipAddressParam,
            Model model) {
        
        List<QueryLog> querylogs;
        
        // Handle combined filtering
        if (queryText != null && !queryText.isEmpty()) {
            querylogs = queryLogService.searchQueryLogs(queryText);
        } else if (userIdParam != null && !userIdParam.isEmpty()) {
            querylogs = queryLogService.getQueryLogsByUserId(userIdParam);
        } else if (ipAddressParam != null && !ipAddressParam.isEmpty()) {
            querylogs = queryLogService.getQueryLogsByIpAddress(ipAddressParam);
        } else {
            querylogs = queryLogService.getAllQueryLogs();
        }
        
        // Calculate statistics for dashboard
        Set<String> uniqueUserSet = querylogs.stream()
                .map(QueryLog::getUserId)
                .collect(Collectors.toSet());
        
        Set<String> uniqueIpSet = querylogs.stream()
                .map(QueryLog::getIpAddress)
                .collect(Collectors.toSet());
        
        model.addAttribute("querylogs", querylogs);
        model.addAttribute("uniqueUsers", uniqueUserSet.size());
        model.addAttribute("uniqueIps", uniqueIpSet.size());
        
        return "querylog";
    }
}