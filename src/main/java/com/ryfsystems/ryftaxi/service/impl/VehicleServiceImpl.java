package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.model.VehicleInfo;
import com.ryfsystems.ryftaxi.repository.VehicleRepository;
import com.ryfsystems.ryftaxi.service.VehicleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleInfo setVehicleInfo(VehicleInfo vehicleInfo) {
        return vehicleRepository.save(vehicleInfo);
    }
}
