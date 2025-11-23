package com.ryfsystems.ryftaxi.dto;

import com.ryfsystems.ryftaxi.model.VehicleInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {

    @Size(min = 3, max = 50)
    private String username;

    @Email(message = "El formato del email no es válido")
    private String email;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "El tipo de Usuario es Obligatorio")
    private Long userType;

    @Nullable
    private VehicleInfo vehicleInfo;
}
