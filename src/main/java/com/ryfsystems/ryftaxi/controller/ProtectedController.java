package com.ryfsystems.ryftaxi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    @PreAuthorize("hasAuthority('ROLE_RIDER') or hasAuthority('ROLE_DRIVER')")
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        String username = authentication.getName();

        return ResponseEntity.ok(Map.of(
                "username", username,
                "message", "Esta es una ruta protegida con JWT",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
