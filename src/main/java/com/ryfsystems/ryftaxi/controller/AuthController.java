package com.ryfsystems.ryftaxi.controller;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.dto.AuthResponse;
import com.ryfsystems.ryftaxi.dto.LoginRequest;
import com.ryfsystems.ryftaxi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "🔐 AuthController",
        description = "Endpoints para registro de usuarios, login, validación de tokens JWT y gestión de sesiones")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Registro de Usuarios", description = "Registro de Usuarios en la Plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "El usuario o email ya existe")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = userService.registerUser(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login de Usuarios", description = "Login de Usuarios en la Plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.loginUser(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response); 
    }

    //@PreAuthorize("hasAnyAuthority()")
    @PostMapping("/logout/{username}")
    @Operation(summary = "Logout de Usuarios", description = "Logout de Usuarios en la Plataforma")
    public ResponseEntity<AuthResponse> logout(@PathVariable String username) {
       userService.logoutUser(username);
       return ResponseEntity.ok(new AuthResponse(true, "Logout exitoso"));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verificacion de email de Riders", description = "Verificacion de email de registro de Riders")
    public ResponseEntity<AuthResponse> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String verificationCode = request.get("verificationCode");

        AuthResponse response = userService.verifyEmail(email, verificationCode);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/verify")
    @Operation(summary = "Respuesta de la Verificación de Usuarios", description = "Respuesta de la Verificación de Usuarios en la Plataforma")
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

    @GetMapping("/approve")
    @Operation(summary = "Respuesta de Aprobación de Drivers", description = "Respuesta de aprobación del Driver")
    public ResponseEntity<String> approveEmailGet(@RequestParam String email) {
        AuthResponse response = userService.approveDriver(email);

        if (response.isSuccess()) {
            return ResponseEntity.ok("<h1>✅ Driver Aprobado exitosamente</h1>");
        } else {
            return ResponseEntity.badRequest().body("<h1>❌ Error verificando email</h1>" +
                    "<p>" + response.getMessage() + "</p>");
        }
    }
}
