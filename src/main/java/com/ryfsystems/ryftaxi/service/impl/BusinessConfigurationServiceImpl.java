package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.model.BusinessConfiguration;
import com.ryfsystems.ryftaxi.repository.BusinessConfigurationRepository;
import com.ryfsystems.ryftaxi.service.BusinessConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusinessConfigurationServiceImpl implements BusinessConfigurationService {

    private final BusinessConfigurationRepository businessConfigurationRepository;

    @Override
    public BusinessConfiguration findById(Long id) {
        return businessConfigurationRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No se encontró la Empresa")
        );
    }
}
