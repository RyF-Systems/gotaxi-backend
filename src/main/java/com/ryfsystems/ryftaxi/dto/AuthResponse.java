package com.ryfsystems.ryftaxi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthResponse {
    private boolean success;
    private String message;
    private String username;
    private Long userStateId;
    private String timestamp;
    private String token;

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    public AuthResponse(boolean success, String message, String username, Long userStateId, String token) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.userStateId = userStateId;
        this.token = token;
        this.timestamp = LocalDateTime.now().toString();
    }
}
