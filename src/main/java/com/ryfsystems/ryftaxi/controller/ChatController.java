package com.ryfsystems.ryftaxi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatWebSocketHandler chatHandler;

    public ChatController(ChatWebSocketHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return Map.of(
                "activeUsers", chatHandler.getActiveUsersCount(),
                "activeRooms", chatHandler.getActiveRoomsCount(),
                "totalSessions", chatHandler.getTotalSessions(),
                "totalMessages", chatHandler.getMessageStats()
        );
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return chatHandler.getDetailedStats();
    }
}
