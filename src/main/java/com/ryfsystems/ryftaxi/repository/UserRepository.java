package com.ryfsystems.ryftaxi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ryfsystems.ryftaxi.dto.AuthResponse;
import com.ryfsystems.ryftaxi.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM users u WHERE u.isOnline = true")
    List<User> findOnlineUsers();

    @Modifying
    @Query("UPDATE users u SET u.isOnline = :isOnline WHERE u.id = :userId")
    Long updateUserOnlineStatus(Long userId, boolean isOnline);

    @Modifying
    @Query("UPDATE users u SET u.lastLogin = :timestamp WHERE u.id = :userId")
    void updateLastLogin(Long userId, String timestamp);

}
