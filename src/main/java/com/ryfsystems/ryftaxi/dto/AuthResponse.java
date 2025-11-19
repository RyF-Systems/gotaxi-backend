package com.ryfsystems.ryftaxi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthResponse {
    private boolean success;
    private String message;
    private String username;
    private String timestamp;
    private String token;

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    public AuthResponse(boolean success, String message, String username, String token) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.token = token;
        this.timestamp = LocalDateTime.now().toString();
    }
}
