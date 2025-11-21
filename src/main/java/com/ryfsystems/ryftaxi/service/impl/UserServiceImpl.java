package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.dto.AuthResponse;
import com.ryfsystems.ryftaxi.dto.LoginRequest;
import com.ryfsystems.ryftaxi.model.User;
import com.ryfsystems.ryftaxi.model.UserRole;
import com.ryfsystems.ryftaxi.model.UserType;
import com.ryfsystems.ryftaxi.repository.UserRepository;
import com.ryfsystems.ryftaxi.repository.UserRoleRepository;
import com.ryfsystems.ryftaxi.service.CustomUserDetailsService;
import com.ryfsystems.ryftaxi.service.EmailVerificationService;
import com.ryfsystems.ryftaxi.service.UserService;
import com.ryfsystems.ryftaxi.service.UserTypeService;
import com.ryfsystems.ryftaxi.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserTypeService userTypeService;
    private final UserRoleRepository userRoleRepository;
    private final CustomUserDetailsService userDetailsService;
    private final EmailVerificationService emailVerificationService;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse registerUser(AuthRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                return new AuthResponse(false, "El nombre de usuario ya está en uso");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                return new AuthResponse(false, "El email ya está registrado");
            }

            UserType userType = userTypeService.findById(request.getUserType());
            if (userType == null) {
                return new AuthResponse(false, "Tipo de usuario no válido");
            }

            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setIsActive(false);
            newUser.setIsOnline(false);
            User saved = userRepository.save(newUser);

            registerRole(saved.getId(), request.getUserType());


            emailVerificationService.sendVerificationEmail(saved);

            return new AuthResponse(true, "Usuario registrado exitosamente, por favor revisa tu e-mail",
                    saved.getUsername(), null);
        } catch (Exception e) {
            return new AuthResponse(false, "Error al registrar el usuario: " + e.getMessage());
        }
    }

    private void registerRole(Long userId, Long userType) {
        UserRole userRole = new UserRole(userId, userType);
        userRoleRepository.save(userRole);
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        try {
            User userOpt = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new Exception("Usuario no encontrado"));

            if (userOpt.getIsActive()) {
                if (!passwordEncoder.matches(request.getPassword(), userOpt.getPassword())) {
                    return new AuthResponse(false, "Usuario / Contraseña incorrecta");
                }

                userOpt.setIsOnline(true);
                userRepository.updateLastLogin(userOpt.getId(), new Date().toString());

                UserDetails userDetails = userDetailsService.loadUserByUsername(userOpt.getUsername());

                String token = jwtUtil.generateToken(userDetails, userOpt);

                return new AuthResponse(true, "Login exitoso", userOpt.getUsername(), token);
            } else {
                return new AuthResponse(false, "Usuario no Está Activado");
            }
        } catch (Exception e) {
            return new AuthResponse(false, "Error en login: " + e.getMessage());
        }
    }

    @Override
    public void logoutUser(String username) {
        userRepository.findByUsername(username)
                .map(user -> userRepository.updateUserOnlineStatus(user.getId(), false))
                .orElseThrow(() -> new RuntimeException("Usuario No Encontrado"));
    }

    @Override
    public User findByUsername(String sender) {
        return this.getUserByUsername(sender).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public void updateRoomAndSession(Long id, String roomId, String sessionId) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        userRepository.updateUserRoomAnSession(user.getId(), roomId, sessionId);
    }

    @Override
    public AuthResponse verifyEmail(String email, String verificationCode) {
        try {
            if (emailVerificationService.verifyEmail(email, verificationCode)) {
                // Activar usuario
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    user.setIsActive(true);
                    userRepository.save(user);

                    // Generar token JWT
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
                    String token = jwtUtil.generateToken(userDetails, user);

                    return new AuthResponse(true, "Email verificado exitosamente", user.getUsername(), token);
                }
            }
            return new AuthResponse(false, "Código de verificación inválido o expirado");
        } catch (Exception e) {
            return new AuthResponse(false, "Error verificando email: " + e.getMessage());
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
