package com.ryfsystems.ryftaxi.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationCode, String username);
}
