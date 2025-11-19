package com.ryfsystems.ryftaxi.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.ryfsystems.ryftaxi.dto.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.dto.AuthResponse;
import com.ryfsystems.ryftaxi.model.User;
import com.ryfsystems.ryftaxi.repository.UserRepository;
import com.ryfsystems.ryftaxi.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse registerUser(AuthRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                return new AuthResponse(false, "El nombre de usuario ya está en uso");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                return new AuthResponse(false, "El email ya está registrado");
            }

            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setIsOnline(false);
            userRepository.save(newUser);

            return new AuthResponse(true, "Usuario registrado exitosamente", newUser.getUsername());
        } catch (Exception e) {
            return new AuthResponse(false, "Error al registrar el usuario: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        try {
            User userOpt = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new Exception( "Usuario no encontrado"));

            if (!passwordEncoder.matches(request.getPassword(), userOpt.getPassword())) {
                return new AuthResponse(false, "Usuario / Contraseña incorrecta");
            }

            userOpt.setIsOnline(true);
            userRepository.updateLastLogin(userOpt.getId(), new Date().toString());

            return new AuthResponse(true, "Login exitoso", userOpt.getUsername());

        } catch (Exception e) {
            return new AuthResponse(false, "Error en login: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse logoutUser(Long id) {
        Long response = userRepository.updateUserOnlineStatus(id, false);
        if (response > 0) {
            return new AuthResponse(true, "Logout exitoso");
        } else {
            return new AuthResponse(false, "Error en logout");
        }
    }

    public List<User> getOnlineUsers() {
        return userRepository.findOnlineUsers();
    }  
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
