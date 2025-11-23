package com.ryfsystems.ryftaxi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity(name = "user_state")
public class UserState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 50, message = "El Nombre del Estado debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String stateName;

    @Size(min = 3, max = 50, message = "La Descripción debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String description;
}
