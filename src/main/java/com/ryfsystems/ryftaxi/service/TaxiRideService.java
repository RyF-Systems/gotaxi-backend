package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.dto.TaxiRideRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaxiRideService {

    TaxiRideRequest createServiceRequest(TaxiRideRequest request);

    TaxiRideRequest acceptServiceRequest(String requestId, Long driverId);

    TaxiRideRequest cancelServiceRequest(String requestId, Long riderId);

    TaxiRideRequest completeServiceRequest(String requestId, Long driverId, Double finalPrice);

    TaxiRideRequest startService(String requestId, Long driverId);

    void loadActiveServicesToCache();

    List<TaxiRideRequest> getPendingRequests();

    TaxiRideRequest getRiderActiveRequest(Long riderId);

    TaxiRideRequest getDriverActiveRequest(Long driverId);

    List<TaxiRideRequest> getRiderHistory(Long riderId);

    List<TaxiRideRequest> getDriverHistory(Long driverId);

    Page<TaxiRideRequest> getAllServices(Pageable pageable);

    Long countAllServices();

    Map<String, Object> countServiceByStatus();

    Optional<TaxiRideRequest> getServiceByRequestId(String requestId);
}
