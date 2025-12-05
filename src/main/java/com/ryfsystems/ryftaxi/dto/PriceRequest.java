package com.ryfsystems.ryftaxi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo para solicitud de precio")
@Builder
public class PriceRequest {

    @Schema(description = "Dirección de recogida", example = "Av. Principal #123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String pickupAddress;

    @Schema(description = "Dirección de destino", example = "Centro Comercial Plaza", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destinationAddress;

    @Schema(description = "Latitud de recogida", example = "4.710989")
    private Double pickupLat;

    @Schema(description = "Longitud de recogida", example = "-74.072092")
    private Double pickupLng;

    @Schema(description = "Latitud de destino", example = "4.698070")
    private Double destinationLat;

    @Schema(description = "Longitud de destino", example = "-74.056595")
    private Double destinationLng;
}
