package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.AuthRequest;
import com.ryfsystems.ryftaxi.model.User;
import com.ryfsystems.ryftaxi.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.sender}")
    private String senderEmail;

    @Value("${app.email.base-url}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String toEmail, String verificationCode, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("🔐 Verifica tu cuenta - RyfTaxi");

            // Contexto para el template
            Context context = new Context(Locale.getDefault());
            context.setVariable("username", username);
            context.setVariable("verificationCode", verificationCode);
            context.setVariable("email", toEmail);
            context.setVariable("baseUrl", baseUrl);
            context.setVariable("supportEmail", "ryftaxi@gmail.com");

            // Procesar template HTML
            String htmlContent = templateEngine.process("email/verification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("✅ Email de verificación enviado a: " + toEmail);

        } catch (MessagingException e) {
            log.error("❌ Error enviando email de verificación: " + e.getMessage());
            throw new RuntimeException("Error enviando email de verificación", e);
        }
    }

    @Override
    public void sendApprovalEmail(User driver, User admin, AuthRequest request) throws MessagingException {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(admin.getEmail());
            helper.setSubject("🔐 Solicitud de Aprobación de Conductor - RyfTaxi");

            // Contexto para el template
            Context context = new Context(Locale.getDefault());
            context.setVariable("adminName", admin.getFirstName() +  " " + admin.getLastName());
            context.setVariable("driverFullName", driver.getFirstName() +  " " + driver.getLastName());
            context.setVariable("driverEmail", driver.getEmail());
            context.setVariable("driverPhone", driver.getPhoneNumber());
            context.setVariable("vehicleInfo", request.getVehicleInfo());
            context.setVariable("verificationStatus", "Pendiente de revisión");
            context.setVariable("requestDate", new Date());
            context.setVariable("baseUrl", baseUrl);

            String htmlContent = templateEngine.process("email/approval", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("✅ Email de verificación enviado a: " + admin.getEmail());
        } catch (MessagingException e) {
            log.error("❌ Error enviando email de verificación: " + e.getMessage());
            throw new RuntimeException("Error enviando email de verificación", e);
        }

    }
}
