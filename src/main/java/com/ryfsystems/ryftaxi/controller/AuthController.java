package com.ryfsystems.ryftaxi.controller;

import com.ryfsystems.ryftaxi.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.dto.AuthResponse;
import com.ryfsystems.ryftaxi.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = userService.registerUser(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.loginUser(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response); 
    }

    @PostMapping("/logout/{username}")
    public ResponseEntity<AuthResponse> logout(@PathVariable String username) {
       userService.logoutUser(username);
       return ResponseEntity.ok(new AuthResponse(true, "Logout exitoso", null, null));
    }
}
