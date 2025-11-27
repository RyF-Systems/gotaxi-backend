package com.ryfsystems.ryftaxi.dto;

import com.ryfsystems.ryftaxi.enums.ServiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxiRideRequest {
    private String requestId;
    private Long riderId;
    private String riderName;
    private Long driverId;
    private String driverName;
    private String pickupAddress;
    private String destinationAddress;
    private Double pickupLat;
    private Double pickupLng;
    private Double destinationLat;
    private Double destinationLng;
    private ServiceStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private Double estimatedPrice;
    private Double finalPrice;
}
