package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.model.User;

public interface EmailVerificationService {

    String generateVerificationCode();
    void sendVerificationEmail(User user);
    void sendApprovalEmail(User user, User admin, AuthRequest request);
    boolean verifyEmail(String email, String verificationCode);
    boolean isEmailVerified(String email);
    boolean canResendVerification(String email);
}
