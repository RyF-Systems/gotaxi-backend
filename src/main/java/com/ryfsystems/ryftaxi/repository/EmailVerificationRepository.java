package com.ryfsystems.ryftaxi.repository;

import com.ryfsystems.ryftaxi.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByVerificationCodeAndEmail(String verificationCode, String email);

    Optional<EmailVerification> findByEmailAndIsUsedFalse(String email);

    @Modifying
    @Query("UPDATE email_verifications ev SET ev.isUsed = true WHERE ev.email = :email")
    void markAllAsUsed(@Param("email") String email);

    @Query("SELECT COUNT(ev) FROM email_verifications ev WHERE ev.email = :email AND ev.createdAt > :since")
    Long countRecentAttempts(@Param("email") String email, @Param("since") LocalDateTime since);
}
