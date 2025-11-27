package com.ryfsystems.ryftaxi.model;

import com.ryfsystems.ryftaxi.dto.TaxiRideRequest;
import com.ryfsystems.ryftaxi.enums.ServiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity(name = "taxi_rides")
@NoArgsConstructor
@AllArgsConstructor
public class TaxiRide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", unique = true, nullable = false)
    private String requestId;

    @Column(name = "rider_id", nullable = false)
    private Long riderId;

    @Column(name = "rider_name", nullable = false)
    private String riderName;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Column(name = "destination_address", nullable = false)
    private String destinationAddress;

    @Column(name = "pickup_lat")
    private Double pickupLat;

    @Column(name = "pickup_lng")
    private Double pickupLng;

    @Column(name = "destination_lat")
    private Double destinationLat;

    @Column(name = "destination_lng")
    private Double destinationLng;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ServiceStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "estimated_price")
    private Double estimatedPrice;

    @Column(name = "final_price")
    private Double finalPrice;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TaxiRide(TaxiRideRequest request) {
        this.requestId = request.getRequestId();
        this.riderId = request.getRiderId();
        this.riderName = request.getRiderName();
        this.driverId = request.getDriverId();
        this.driverName = request.getDriverName();
        this.pickupAddress = request.getPickupAddress();
        this.destinationAddress = request.getDestinationAddress();
        this.pickupLat = request.getPickupLat();
        this.pickupLng = request.getPickupLng();
        this.destinationLat = request.getDestinationLat();
        this.destinationLng = request.getDestinationLng();
        this.status = request.getStatus();
        this.requestedAt = request.getRequestedAt();
        this.acceptedAt = request.getAcceptedAt();
        this.completedAt = request.getCompletedAt();
        this.estimatedPrice = request.getEstimatedPrice();
        this.finalPrice = request.getFinalPrice();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public TaxiRideRequest toTaxiServiceRequest() {
        TaxiRideRequest request = new TaxiRideRequest();
        request.setRequestId(this.requestId);
        request.setRiderId(this.riderId);
        request.setRiderName(this.riderName);
        request.setDriverId(this.driverId);
        request.setDriverName(this.driverName);
        request.setPickupAddress(this.pickupAddress);
        request.setDestinationAddress(this.destinationAddress);
        request.setPickupLat(this.pickupLat);
        request.setPickupLng(this.pickupLng);
        request.setDestinationLat(this.destinationLat);
        request.setDestinationLng(this.destinationLng);
        request.setStatus(this.status);
        request.setRequestedAt(this.requestedAt);
        request.setAcceptedAt(this.acceptedAt);
        request.setCompletedAt(this.completedAt);
        request.setEstimatedPrice(this.estimatedPrice);
        request.setFinalPrice(this.finalPrice);
        return request;
    }
}
