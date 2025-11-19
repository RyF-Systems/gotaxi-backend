package com.ryfsystems.ryftaxi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity(name = "user_roles")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "UserId obligatorio")
    @Size(min = 1, message = "Debe tener al menos 1 caracter")
    @Column(nullable = false, length = 255)
    private Long userId;

    @NotBlank(message = "TypeId obligatorio")
    @Size(min = 1, message = "Debe tener al menos 1 caracter")
    @Column(nullable = false, length = 255)
    private Long userTypeId;
}
