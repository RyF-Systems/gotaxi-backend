package com.ryfsystems.ryftaxi.repository;

import com.ryfsystems.ryftaxi.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);

    List<UserRole> findByUserTypeId(Long userTypeId);
}
