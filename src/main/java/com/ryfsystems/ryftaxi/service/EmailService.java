package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.model.User;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationCode, String username);

    void sendApprovalEmail(User driver, User admin, AuthRequest request) throws MessagingException;
}
