package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.model.UserRole;
import com.ryfsystems.ryftaxi.repository.UserRoleRepository;
import com.ryfsystems.ryftaxi.service.UserRoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Override
    public List<UserRole> findByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId);
    }

    @Override
    public List<UserRole> findByRoleId(Long roleId) {
        return userRoleRepository.findByUserTypeId(roleId);
    }

    @Override
    public UserRole findByUserIdAndRoleId(Long userId, Long roleId) {
        return userRoleRepository.findByUserIdAndUserTypeId(userId, roleId).orElseThrow(
                () -> new RuntimeException("Usuario no Encontrado")
        );
    }

    @Override
    public void save(UserRole userRole) {
        userRoleRepository.save(userRole);
    }
}
