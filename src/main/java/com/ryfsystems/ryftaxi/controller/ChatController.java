package com.ryfsystems.ryftaxi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
@RequestMapping("/api/chat")
@Tag(name = "ChatController", description = "Gestión de Estadísticas en tiempo real")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {
    private final ChatWebSocketHandler chatHandler;

    public ChatController(ChatWebSocketHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/stats")
    @Operation(summary = "Obtiene Estadísticas parciales en tiempo Real", description = "Estadísticas parciales en tiempo Real")
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
    @Operation(summary = "Obtiene las Estadísticas Detalladas en tiempo Real", description = "Estadísticas Detalladas en tiempo Real")
    public Map<String, Object> getStatistics() {
        return chatHandler.getDetailedStats();
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/full-stats")
    @Operation(summary = "Obtiene Todas las Estadísticas en tiempo Real", description = "Estadísticas Totales en tiempo Real")
    public Map<String, Object> getFullStats() {
        return chatHandler.getFullStats();
    }
}
