package com.ryfsystems.ryftaxi.model;

import com.ryfsystems.ryftaxi.enums.Gender;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Email(message = "El formato del email no es válido")
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_picture_url")
    @Nullable
    private String profilePictureUrl;
    
    @Nullable
    private String cedula;

    @Column(name = "session_id")
    @Nullable
    private String sessionId;

    @Column(name = "current_room")
    @Nullable
    private String currentRoom;
    
    @Column(name = "created_at", updatable = false)
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "last_login")
    @Nullable
    private String lastLogin;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "available")
    private Boolean available;

    public User() {
    }

    public User(Long id, String username, String sessionId) {
        this.id = id;
        this.username = username;
        this.sessionId = sessionId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now().toString();
        updatedAt = LocalDateTime.now().toString();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now().toString();
    }
}
