package com.ryfsystems.ryftaxi.repository;

import com.ryfsystems.ryftaxi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    @Query("UPDATE users u SET u.isOnline = :isOnline, u.available = :isAvailable WHERE u.id = :userId")
    Long updateUserStatusAndAvailability(Long userId, boolean isOnline, boolean isAvailable);

    @Modifying
    @Query("UPDATE users u SET u.lastLogin = :timestamp WHERE u.id = :userId")
    void updateLastLogin(Long userId, String timestamp);

    @Modifying
    @Query("UPDATE users u SET u.currentRoom = :roomId, u.sessionId = :sessionId WHERE u.id = :id")
    void updateUserRoomAnSession(Long id, String roomId, String sessionId);

    @Query("Select u from users u join user_roles ur on u.id = ur.userId where ur.userTypeId = 2 ORDER BY u.id ASC LIMIT 1")
    User findFirstAdmin();
}
