package com.ryfsystems.ryftaxi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ryfsystems.ryftaxi.model.BusinessConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessConfigurationRepository extends JpaRepository<BusinessConfiguration, Long> {

}
