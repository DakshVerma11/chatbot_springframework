package com.chatbot.controller;

import com.chatbot.dto.ChatMessage;
import com.chatbot.service.ChatbotService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class WebSocketController {

    private final ChatbotService chatbotService;

    public WebSocketController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        System.out.println("Received message: " + chatMessage.getContent());
        String botResponse = chatbotService.processMessage(chatMessage.getContent());
        System.out.println("Sending response: " + botResponse);
        return new ChatMessage(ChatMessage.MessageType.CHAT, botResponse, "Chatbot");
    }

    @MessageMapping("/chat.newUser")
    @SendTo("/topic/public")
    public ChatMessage newUser(@Payload ChatMessage chatMessage, 
                              SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("New user joined: " + chatMessage.getSender());
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return new ChatMessage(ChatMessage.MessageType.JOIN,
                              "Hello! How can I help you today?",
                              "Chatbot");
    }
}