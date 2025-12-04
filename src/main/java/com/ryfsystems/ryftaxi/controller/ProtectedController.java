package com.ryfsystems.ryftaxi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/protected")
@Tag(name = "ProtectedController", description = "Controlador Protegido")
@SecurityRequirement(name = "bearerAuth")
public class ProtectedController {

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_RIDER') or hasAuthority('ROLE_DRIVER')")
    @Operation(summary = "Obtiene el perfil del Usuario", description = "Perfil de Usuario")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(Map.of(
                "username", username,
                "message", "Esta es una ruta protegida con JWT",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
