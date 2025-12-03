package com.ryfsystems.ryftaxi.dto;

import com.ryfsystems.ryftaxi.enums.ServiceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo para solicitud de viaje en taxi")
public class TaxiRideRequest {

    @Schema(description = "ID único de la solicitud", example = "req_1701945000000_123", readOnly = true)
    private String requestId;

    @Schema(description = "ID del pasajero (rider)", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long riderId;

    @Schema(description = "Nombre del pasajero", example = "Juan Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String riderName;

    @Schema(description = "ID del conductor asignado", example = "456", readOnly = true)
    private Long driverId;

    @Schema(description = "Nombre del conductor", example = "María García", readOnly = true)
    private String driverName;

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

    @Schema(
            description = "Estado del servicio",
            example = "PENDING",
            readOnly = true,
            allowableValues = {"PENDING", "ACCEPTED", "IN_PROGRESS", "COMPLETED", "CANCELLED", "REJECTED"}
    )
    private ServiceStatus status;

    @Schema(description = "Fecha y hora de la solicitud", example = "2023-12-07T10:30:00", readOnly = true)
    private LocalDateTime requestedAt;

    @Schema(description = "Fecha y hora de aceptación", example = "2023-12-07T10:32:00", readOnly = true)
    private LocalDateTime acceptedAt;

    @Schema(description = "Fecha y hora de aceptación", example = "2023-12-07T10:32:00", readOnly = true)
    private LocalDateTime completedAt;

    @Schema(description = "Precio estimado del viaje", example = "15000.0")
    private Double estimatedPrice;

    @Schema(description = "Precio final del viaje", example = "14500.0")
    private Double finalPrice;
}
