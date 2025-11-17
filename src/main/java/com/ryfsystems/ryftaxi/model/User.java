package com.ryfsystems.ryftaxi.model;

import lombok.Data;
import com.ryfsystems.ryftaxi.enums.UserType;

@Data
public class User {
    private String id;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private String cedula;
    private String sessionId;
    private String currentRoom;
    private UserType UserType;

    public User() {
    }

    public User(String id, String username, String sessionId) {
        this.id = id;
        this.username = username;
        this.sessionId = sessionId;
    }
}
