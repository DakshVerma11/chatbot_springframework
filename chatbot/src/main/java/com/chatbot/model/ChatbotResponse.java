package com.chatbot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chatbot_data")
public class ChatbotResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String keywords; // Comma-separated keywords

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "link_text")
    private String linkText;

    @Column(name = "link_url", columnDefinition = "TEXT")
    private String linkUrl;

    public ChatbotResponse() {
    }

    public ChatbotResponse(Long id, String keywords, String response, String linkText, String linkUrl) {
        this.id = id;
        this.keywords = keywords;
        this.response = response;
        this.linkText = linkText;
        this.linkUrl = linkUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    // Helper method to check if this response has a link
    public boolean hasLink() {
        return linkText != null && !linkText.isEmpty() && 
               linkUrl != null && !linkUrl.isEmpty();
    }
}