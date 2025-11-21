package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.model.User;

public interface EmailVerificationService {

    String generateVerificationCode();
    void sendVerificationEmail(User user);
    boolean verifyEmail(String email, String verificationCode);
    boolean isEmailVerified(String email);
    boolean canResendVerification(String email);
}
