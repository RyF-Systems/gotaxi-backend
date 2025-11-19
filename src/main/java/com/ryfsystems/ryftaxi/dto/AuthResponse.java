package com.ryfsystems.ryftaxi.dto;

import java.time.LocalDateTime;

import lombok.Data;

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

    public AuthResponse(boolean success, String message, String username) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.timestamp = LocalDateTime.now().toString();
    }
}
