package com.ryfsystems.ryftaxi.repository;

import com.ryfsystems.ryftaxi.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);

    List<UserRole> findByUserTypeId(Long userTypeId);

    Optional<UserRole> findByUserIdAndUserTypeId(Long userId, Long userTypeId);
}
