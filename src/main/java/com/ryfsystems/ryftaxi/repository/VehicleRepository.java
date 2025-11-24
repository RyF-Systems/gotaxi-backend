package com.ryfsystems.ryftaxi.repository;

import com.ryfsystems.ryftaxi.model.VehicleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleInfo, Long> {
}
