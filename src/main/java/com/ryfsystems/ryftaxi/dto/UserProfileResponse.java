package com.ryfsystems.ryftaxi.dto;

import com.ryfsystems.ryftaxi.enums.Gender;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String cedula;
    private String phoneNumber;
    private Gender gender;
    private Boolean isActive;
    private Boolean isOnline;
    private String lastLogin;
    private String createdAt;
    private String updatedAt;
    private String profilePictureUrl;
    private String currentRoom;
    private String sessionId;
    private List<String> userRoles;
}
