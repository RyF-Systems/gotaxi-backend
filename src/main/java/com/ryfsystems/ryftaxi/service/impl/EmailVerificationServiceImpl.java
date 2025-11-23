package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.model.EmailVerification;
import com.ryfsystems.ryftaxi.model.User;
import com.ryfsystems.ryftaxi.repository.EmailVerificationRepository;
import com.ryfsystems.ryftaxi.service.EmailService;
import com.ryfsystems.ryftaxi.service.EmailVerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;

    @Value("${app.email.verification.expiration-minutes}")
    private int expirationMinutes;

    @Value("${app.email.verification.enabled}")
    private boolean emailVerificationEnabled;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    @Override
    public void sendVerificationEmail(User user) {
        if (emailVerification()) return;

        // Invalidar códigos anteriores
        emailVerificationRepository.markAllAsUsed(user.getEmail());

        // Generar nuevo código
        String verificationCode = generateVerificationCode();

        // Guardar en base de datos
        EmailVerification emailVerification = new EmailVerification(user.getId(), user.getEmail(), verificationCode, expirationMinutes);
        emailVerificationRepository.save(emailVerification);

        // Enviar email
        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationCode, user.getUsername());
        } catch (Exception e) {
            // En desarrollo, simular envío
            //emailService.sendVerificationEmailDev(user.getEmail(), verificationCode, user.getUsername());
        }
    }

    private boolean emailVerification() {
        if (!emailVerificationEnabled) {
            log.info("⚠️ Verificación por email deshabilitada");
            return true;
        }
        return false;
    }

    @Override
    public void sendApprovalEmail(User user, User admin, AuthRequest request) {
        if (emailVerification()) return;
        try {
            emailService.sendApprovalEmail(user, admin, request);
        } catch (Exception e) {
            // En desarrollo, simular envío
            //emailService.sendVerificationEmailDev(user.getEmail(), verificationCode, user.getUsername());
        }
    }

    @Override
    public boolean verifyEmail(String email, String verificationCode) {
        Optional<EmailVerification> verificationOpt =
                emailVerificationRepository.findByVerificationCodeAndEmail(verificationCode, email);

        if (verificationOpt.isPresent()) {
            EmailVerification verification = verificationOpt.get();

            if (verification.isValid()) {
                verification.setIsUsed(true);
                emailVerificationRepository.save(verification);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmailVerified(String email) {
        if (!emailVerificationEnabled) {
            return true;
        }

        Optional<EmailVerification> verificationOpt =
                emailVerificationRepository.findByEmailAndIsUsedFalse(email);

        return verificationOpt.map(EmailVerification::isValid).orElse(false);
    }

    public boolean canResendVerification(String email) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        Long recentAttempts = emailVerificationRepository.countRecentAttempts(email, oneMinuteAgo);
        return recentAttempts == null || recentAttempts < 3;
    }

}
