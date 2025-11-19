package com.ryfsystems.ryftaxi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

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
