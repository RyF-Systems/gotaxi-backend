package com.ryfsystems.ryftaxi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long userTypeId;

    public UserRole() {
    }

    public UserRole(Long userId, Long userTypeId) {
        this.userId = userId;
        this.userTypeId = userTypeId;
    }

}
