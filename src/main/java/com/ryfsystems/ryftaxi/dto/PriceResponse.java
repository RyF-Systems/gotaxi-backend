package com.ryfsystems.ryftaxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo para la Respuesta de precio")
@Builder
public class PriceResponse {

    @Schema(description = "Precio estimado del viaje en $", example = "15000.0")
    private Double estimatedUsdPrice;

    @Schema(description = "Subtotal antes de impuesto del viaje en Bs", example = "15000.0")
    private Double subtotalBs;

    @Schema(description = "Precio estimado del viaje en Bs", example = "15000.0")
    private Double estimatedBsPrice;

    @Schema(description = "Tasa de Impuesto en Bs", example = "16.0")
    private Double taxIva;

    @Schema(description = "Tarifa de Impuesto en Bs", example = "126.0")
    private Double amountIva;

    @Schema(description = "Distancia del viaje en Km", example = "126.0")
    private Double distance;
}
