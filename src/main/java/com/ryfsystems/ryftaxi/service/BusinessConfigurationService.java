package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.dto.BusinessConfigurationDTO;
import com.ryfsystems.ryftaxi.model.BusinessConfiguration;
import jakarta.validation.Valid;

public interface BusinessConfigurationService {

    BusinessConfiguration findById(Long id);

    BusinessConfigurationDTO update(Long id, @Valid BusinessConfigurationDTO patchDTO);
}
