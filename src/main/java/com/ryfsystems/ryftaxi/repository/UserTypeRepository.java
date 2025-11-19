package com.ryfsystems.ryftaxi.repository;

import com.ryfsystems.ryftaxi.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {

    @Query("select ut from user_types ut where ut.id >= 3")
    List<UserType> findAvailableUserTypes();

    List<UserType> findByIdIn(List<Long> ids);

    @Query("SELECT ut FROM user_types ut " +
            "JOIN user_roles ur ON ut.id = ur.userTypeId " +
            "WHERE ur.userId = :userId")
    List<UserType> findByUserId(@Param("userId") Long userId);
}
