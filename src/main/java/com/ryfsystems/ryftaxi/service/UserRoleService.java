package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.model.UserRole;

import java.util.List;

public interface UserRoleService {

    List<UserRole> findByUserId(Long userId);
    List<UserRole> findByRoleId(Long roleId);
}
