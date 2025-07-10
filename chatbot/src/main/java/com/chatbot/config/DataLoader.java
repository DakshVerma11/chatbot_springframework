package com.chatbot.config;

import com.chatbot.model.ChatbotResponse;
import com.chatbot.repository.ChatbotResponseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final ChatbotResponseRepository repository;
    private boolean alreadySetup = false;

    @Autowired
    public DataLoader(ChatbotResponseRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        if (repository.count() > 0) {
            System.out.println("Data already loaded, skipping initialization.");
            alreadySetup = true;
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data/chatbot-responses.json").getInputStream();

            List<JsonNode> responseNodes = mapper.readValue(
                    inputStream,
                    new TypeReference<List<JsonNode>>() {}
            );

            List<ChatbotResponse> responses = new ArrayList<>();

            for (JsonNode node : responseNodes) {
                ChatbotResponse response = new ChatbotResponse();
                
                // Set ID if present
                if (node.has("id")) {
                    response.setId(node.get("id").asLong());
                }
                
                // Handle keywords (convert array to comma-separated string)
                if (node.has("keywords") && node.get("keywords").isArray()) {
                    StringBuilder keywordsBuilder = new StringBuilder();
                    for (JsonNode keyword : node.get("keywords")) {
                        if (keywordsBuilder.length() > 0) {
                            keywordsBuilder.append(",");
                        }
                        keywordsBuilder.append(keyword.asText());
                    }
                    response.setKeywords(keywordsBuilder.toString());
                }
                
                // Set the response text
                if (node.has("response")) {
                    response.setResponse(node.get("response").asText());
                }
                
                // Handle link if present
                if (node.has("link")) {
                    JsonNode link = node.get("link");
                    if (link.has("text")) {
                        response.setLinkText(link.get("text").asText());
                    }
                    if (link.has("url")) {
                        response.setLinkUrl(link.get("url").asText());
                    }
                }
                
                responses.add(response);
            }

            repository.saveAll(responses);
            System.out.println("Successfully loaded " + responses.size() + " responses.");
            alreadySetup = true;

        } catch (Exception e) {
            System.err.println("Error loading initial data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}