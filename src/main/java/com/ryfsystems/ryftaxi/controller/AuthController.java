package com.ryfsystems.ryftaxi.controller;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.dto.AuthResponse;
import com.ryfsystems.ryftaxi.dto.LoginRequest;
import com.ryfsystems.ryftaxi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PreAuthorize("hasAnyAuthority()")
    @PostMapping("/logout/{username}")
    public ResponseEntity<AuthResponse> logout(@PathVariable String username) {
       userService.logoutUser(username);
       return ResponseEntity.ok(new AuthResponse(true, "Logout exitoso", null, null));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String verificationCode = request.get("verificationCode");

        AuthResponse response = userService.verifyEmail(email, verificationCode);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmailGet(@RequestParam String code, @RequestParam String email) {
        AuthResponse response = userService.verifyEmail(email, code);

        if (response.isSuccess()) {
            return ResponseEntity.ok("<h1>✅ Email verificado exitosamente</h1>" +
                    "<p>Ya puedes iniciar sesión en la aplicación.</p>" +
                    "<a href='/login.html'>Ir al login</a>");
        } else {
            return ResponseEntity.badRequest().body("<h1>❌ Error verificando email</h1>" +
                    "<p>" + response.getMessage() + "</p>");
        }
    }
}
