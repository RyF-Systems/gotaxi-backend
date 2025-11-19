package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.model.UserType;
import com.ryfsystems.ryftaxi.repository.UserTypeRepository;
import com.ryfsystems.ryftaxi.service.UserTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserTypeServiceImpl implements UserTypeService {

    private final UserTypeRepository repository;

    @Override
    public UserType findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de usuario no válido"));
    }

    @Override
    public List<UserType> getAvailableUserTypes() {
        return repository.findAvailableUserTypes();
    }
}
