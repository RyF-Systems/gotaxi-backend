package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.dto.AuthResponse;
import com.ryfsystems.ryftaxi.dto.LoginRequest;
import com.ryfsystems.ryftaxi.dto.UserProfileResponse;
import com.ryfsystems.ryftaxi.model.User;
import com.ryfsystems.ryftaxi.model.UserRole;
import com.ryfsystems.ryftaxi.model.UserType;
import com.ryfsystems.ryftaxi.repository.UserRepository;
import com.ryfsystems.ryftaxi.service.*;
import com.ryfsystems.ryftaxi.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRoleService userRoleService;
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
            User saved = saveNewUser(request);
            if (request.getUserType() == 3) {
                registerRole(saved.getId(), request.getUserType(), 1L);
                User admin = this.findFirstAdmin();
                emailVerificationService.sendApprovalEmail(saved, admin, request);
                return new AuthResponse(true, "Usuario registrado exitosamente en espera de aprobación",
                        saved.getUsername(), null, null);
            } else if (request.getUserType() == 4) {
                registerRole(saved.getId(), request.getUserType(), 3L);
                emailVerificationService.sendVerificationEmail(saved);
                return new AuthResponse(true, "Usuario registrado exitosamente, por favor revisa tu e-mail",
                        saved.getUsername(), null, null);
            } else {
                return new AuthResponse(true, "Usuario registrado exitosamente",
                        saved.getUsername(), null, null);
            }
        } catch (Exception e) {
            return new AuthResponse(false, "Error al registrar el usuario: " + e.getMessage());
        }
    }

    private User saveNewUser(AuthRequest request) {
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setIsOnline(false);
        return userRepository.save(newUser);
    }

    private void registerRole(Long userId, Long userType, Long userStateId) {
        UserRole userRole = new UserRole(userId, userType, userStateId);
        userRoleService.save(userRole);
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        try {
            User userOpt = userRepository.findByUsername(request.getUsername()).orElseThrow(
                    () -> new Exception("Usuario no encontrado"));
            UserRole ur = userRoleService.findByUserIdAndRoleId(userOpt.getId(), request.getRoleId());
            if (!passwordEncoder.matches(request.getPassword(), userOpt.getPassword())) {
                return new AuthResponse(false, "Usuario / Contraseña incorrecta");
            }
            userOpt.setIsOnline(true);
            if (ur.getUserTypeId() == 1 || ur.getUserTypeId() == 2 || ur.getUserTypeId() == 4) {
                userOpt.setAvailable(true);
            }
            if (ur.getUserStateId() == 1 || ur.getUserStateId() == 2) {
                userRepository.updateLastLogin(userOpt.getId(), new Date().toString());
                UserDetails userDetails = userDetailsService.loadUserByUsername(userOpt.getUsername());
                String token = jwtUtil.generateToken(userDetails, userOpt);
                return new AuthResponse(true, "Login exitoso", userOpt.getUsername(), ur.getUserStateId(), token);
            } else if (ur.getUserStateId() == 3) {
                return new AuthResponse(false, "Usuario no Está Activado");
            } else if (ur.getUserStateId() == 4) {
                return new AuthResponse(false, "Usuario Baneado");
            } else {
                userRepository.updateLastLogin(userOpt.getId(), new Date().toString());
                UserDetails userDetails = userDetailsService.loadUserByUsername(userOpt.getUsername());
                String token = jwtUtil.generateToken(userDetails, userOpt);
                return new AuthResponse(true, "Login exitoso", userOpt.getUsername(), ur.getUserStateId(), token);
            }
        } catch (Exception e) {
            return new AuthResponse(false, "Error en login: " + e.getMessage());
        }
    }

    @Override
    public void logoutUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario No Encontrado"));
        userRepository.updateUserStatusAndAvailability(user.getId(), false, false);
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
                    UserRole userRole = userRoleService.findByUserIdAndRoleId(user.getId(), 4L);
                    userRole.setUserStateId(2L);
                    userRepository.save(user);
                    return new AuthResponse(true, "Email verificado exitosamente");
                }
            }
            return new AuthResponse(false, "Código de verificación inválido o expirado");
        } catch (Exception e) {
            return new AuthResponse(false, "Error verificando email: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElseThrow(
                    () -> new RuntimeException("Usuario no encontrado"));

            List<Long> userRoles = userRoleService.findByUserId(user.getId())
                    .stream().map(UserRole::getUserTypeId).toList();
            List<String> userTypes = userTypeService.getByIdIn(userRoles)
                    .stream().map(UserType::getTypeName).toList();

            UserProfileResponse profile = getUserProfileResponse(user, userTypes);

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.ok("Error obteniendo perfil: " + e.getMessage());
        }
    }

    private static UserProfileResponse getUserProfileResponse(User user, List<String> userTypes) {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setCedula(user.getCedula());
        profile.setPhoneNumber(user.getPhoneNumber());
        profile.setGender(user.getGender());
        profile.setIsOnline(user.getIsOnline());
        profile.setLastLogin(user.getLastLogin());
        profile.setCreatedAt(user.getCreatedAt());
        profile.setUpdatedAt(user.getUpdatedAt());
        profile.setProfilePictureUrl(user.getProfilePictureUrl());
        profile.setCurrentRoom(user.getCurrentRoom());
        profile.setSessionId(user.getSessionId());
        profile.setUserRoles(userTypes);
        return profile;
    }

    @Override
    public User findFirstAdmin() {
        return userRepository.findFirstAdmin();
    }

    @Override
    public AuthResponse approveDriver(String email) {
        try {
            // Activar usuario
            User userOpt = userRepository.findByEmail(email).orElseThrow(
                    () -> new RuntimeException("Usuario no encontrado"));
            UserRole userRole = userRoleService.findByUserIdAndRoleId(userOpt.getId(), 3L);
            userRole.setUserStateId(2L);
            userRepository.save(userOpt);
            return new AuthResponse(true, "Driver Aprobado exitosamente");
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
