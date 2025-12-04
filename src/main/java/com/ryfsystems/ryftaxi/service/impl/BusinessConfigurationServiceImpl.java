package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.BusinessConfigurationDTO;
import com.ryfsystems.ryftaxi.dto.Phone;
import com.ryfsystems.ryftaxi.model.BusinessConfiguration;
import com.ryfsystems.ryftaxi.repository.BusinessConfigurationRepository;
import com.ryfsystems.ryftaxi.service.BusinessConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessConfigurationServiceImpl implements BusinessConfigurationService {

    private final BusinessConfigurationRepository businessConfigurationRepository;

    @Override
    public BusinessConfiguration findById(Long id) {
        return businessConfigurationRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No se encontró la Empresa")
        );
    }

    @Override
    @Transactional
    public BusinessConfigurationDTO update(Long id, BusinessConfigurationDTO patchDTO) {
        log.info("Actualización parcial de configuración con ID: {}", id);

        BusinessConfiguration existingConfig = this.findById(id);
        existingConfig.setRif(patchDTO.rif());
        existingConfig.setName(patchDTO.name());
        existingConfig.setBasePriceUsd(patchDTO.basePriceUsd());
        existingConfig.setFractionPriceUsd(patchDTO.fractionPriceUsd());
        existingConfig.setTaxIva(patchDTO.taxIva());

        if(patchDTO.phoneList() != null) {
            List<Phone> uniquePhones = patchDTO.phoneList()
                    .stream()
                    .distinct()
                    .toList();
            existingConfig.setPhoneList(uniquePhones);
        }

        return this.toDto(businessConfigurationRepository.save(existingConfig));
    }

    private BusinessConfigurationDTO toDto(BusinessConfiguration config) {
        return new BusinessConfigurationDTO(
                config.getId(),
                config.getName(),
                config.getRif(),
                config.getBasePriceUsd(),
                config.getFractionPriceUsd(),
                config.getTaxIva(),
                config.getPhoneList() != null ? config.getPhoneList() : List.of()
        );
    }
}
