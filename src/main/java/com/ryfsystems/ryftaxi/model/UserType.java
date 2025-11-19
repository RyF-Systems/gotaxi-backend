package com.ryfsystems.ryftaxi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity(name = "user_types")
public class UserType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El Nombre del Tipo es obligatorio")
    @Size(min = 3, max = 50, message = "El Nombre del Tipo debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String typeName;

    @NotBlank(message = "La Descripcion es obligatoria")
    @Size(min = 3, max = 50, message = "La Descripción debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String description;
}
