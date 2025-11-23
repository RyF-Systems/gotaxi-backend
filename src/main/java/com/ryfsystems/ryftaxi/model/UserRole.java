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
    private Long userStateId;

    public UserRole() {
    }

    public UserRole(Long userId, Long userTypeId, Long userStateId) {
        this.userId = userId;
        this.userTypeId = userTypeId;
        this.userStateId = userStateId;
    }

}
