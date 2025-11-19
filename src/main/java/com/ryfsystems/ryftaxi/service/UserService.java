package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.dto.AuthResponse;
import com.ryfsystems.ryftaxi.dto.LoginRequest;

public interface UserService {

    AuthResponse registerUser(AuthRequest request);
    AuthResponse loginUser(LoginRequest request);
    AuthResponse logoutUser(Long id);
}
