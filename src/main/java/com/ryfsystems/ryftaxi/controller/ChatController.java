package com.ryfsystems.ryftaxi.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatWebSocketHandler chatHandler;
    
    public ChatController(ChatWebSocketHandler chatHandler) {
        this.chatHandler = chatHandler;
    }
    
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return Map.of(
            "activeUsers", chatHandler.getActiveUsersCount(),
            "activeRooms", chatHandler.getActiveRoomsCount(),
            "totalSessions", chatHandler.getTotalSessions()
        );
    }
}
