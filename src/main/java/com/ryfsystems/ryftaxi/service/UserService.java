package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.dto.AuthResponse;
import com.ryfsystems.ryftaxi.dto.LoginRequest;
import com.ryfsystems.ryftaxi.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {

    AuthResponse registerUser(AuthRequest request);
    AuthResponse loginUser(LoginRequest request);
    void logoutUser(String username);

    User findByUsername(String sender);
    void updateRoomAndSession(Long id, String roomId, String sessionId);
    AuthResponse verifyEmail(String email, String verificationCode);

    ResponseEntity<?> getUserProfile();
}
