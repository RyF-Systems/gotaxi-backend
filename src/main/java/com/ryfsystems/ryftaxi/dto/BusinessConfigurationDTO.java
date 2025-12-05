package com.ryfsystems.ryftaxi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BusinessConfigurationDTO(
        Long id,

        @Size(min = 3, max = 50, message = "El name debe tener entre 3 y 50 caracteres")
        String name,

        @Size(min = 12, max = 12, message = "El rif debe tener 12 caracteres")
        String rif,

        @DecimalMin(value = "0.0", message = "El precio base debe ser mayor o igual a 0")
        Double basePriceUsd,

        @DecimalMin(value = "0.0", message = "El precio por fracción debe ser mayor o igual a 0")
        Double fractionPriceUsd,

        @DecimalMin(value = "0.0", message = "El IVA debe ser mayor o igual a 0")
        Double taxIva,

        @DecimalMin(value = "0.0", message = "La distancia de Carrera minima debe ser mayor o igual a 0")
        Double minDistanceBonus,

        List<Phone> phoneList
) {
}
