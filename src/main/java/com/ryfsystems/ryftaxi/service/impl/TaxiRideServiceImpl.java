package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.PriceRequest;
import com.ryfsystems.ryftaxi.dto.PriceResponse;
import com.ryfsystems.ryftaxi.dto.TaxiRideRequest;
import com.ryfsystems.ryftaxi.enums.ServiceStatus;
import com.ryfsystems.ryftaxi.model.TaxiRide;
import com.ryfsystems.ryftaxi.model.User;
import com.ryfsystems.ryftaxi.repository.TaxiRideRepository;
import com.ryfsystems.ryftaxi.service.FareCalculationService;
import com.ryfsystems.ryftaxi.service.TaxiRideService;
import com.ryfsystems.ryftaxi.service.UserRoleService;
import com.ryfsystems.ryftaxi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxiRideServiceImpl implements TaxiRideService {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final TaxiRideRepository taxiRideRepository;
    private final FareCalculationService fareCalculationService;

    private final Map<String, TaxiRideRequest> activeRequests = new ConcurrentHashMap<>();
    private final Map<Long, String> riderToRequestMap = new ConcurrentHashMap<>();
    private final Map<Long, String> driverToRequestMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public TaxiRideRequest createServiceRequest(TaxiRideRequest request) {
        User rider = userService.findById(request.getRiderId());
        if (rider == null || userRoleService.existsByUserIdAndRoleId(rider.getId(), 3L)) {
            throw new IllegalArgumentException("Solo los usuarios tipo rider pueden solicitar servicios");
        }
        Optional<TaxiRide> activeService = taxiRideRepository.findActiveServiceByRider(request.getRiderId());
        if (activeService.isPresent()) {
            throw new IllegalArgumentException("Ya tienes un servicio activo");
        }
        request.setRequestId(this.generateRequestId(request));
        request.setStatus(ServiceStatus.PENDING);
        request.setRequestedAt(LocalDateTime.now());

        TaxiRide taxiRide = new TaxiRide(request);
        TaxiRide saved = taxiRideRepository.save(taxiRide);

        TaxiRideRequest savedRequest = saved.toTaxiServiceRequest();
        activeRequests.put(savedRequest.getRequestId(), savedRequest);
        riderToRequestMap.put(savedRequest.getRiderId(), savedRequest.getRequestId());
        log.info("💾 Solicitud de servicio guardada en BD - ID: {}, Rider: {}",
                savedRequest.getRequestId(), savedRequest.getRiderName());
        return savedRequest;
    }

    private String generateRequestId(TaxiRideRequest request) {
        return LocalDateTime.now() + "_" + request.getRiderId();
    }

    @Override
    @Transactional
    public TaxiRideRequest acceptServiceRequest(String requestId, Long driverId) {
        User driver = userService.findById(driverId);
        if (driver == null || userRoleService.existsByUserIdAndRoleId(driver.getId(), 4L)) {
            throw new IllegalArgumentException("Solo los usuarios tipo Driver pueden Aceptar servicios");
        }
        Optional<TaxiRide> activeDriverService = taxiRideRepository.findActiveServiceByDriver(driverId);
        if (activeDriverService.isPresent()) {
            throw new IllegalArgumentException("Ya tienes un servicio activo");
        }
        TaxiRide taxiRide = taxiRideRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        if (taxiRide.getStatus() != ServiceStatus.PENDING) {
            throw new IllegalArgumentException("La solicitud ya no está disponible");
        }
        taxiRide.setDriverId(driverId);
        taxiRide.setDriverName(driver.getUsername());
        taxiRide.setStatus(ServiceStatus.ACCEPTED);
        taxiRide.setAcceptedAt(LocalDateTime.now());
        taxiRide.setUpdatedAt(LocalDateTime.now());
        TaxiRide updatedEntity = taxiRideRepository.save(taxiRide);
        TaxiRideRequest updatedRequest = updatedEntity.toTaxiServiceRequest();
        activeRequests.put(requestId, updatedRequest);
        driverToRequestMap.put(driverId, requestId);

        log.info("✅ Servicio aceptado en BD - Request: {}, Driver: {}", requestId, driverId);
        return updatedRequest;
    }

    @Override
    @Transactional
    public TaxiRideRequest cancelServiceRequest(String requestId, Long riderId) {
        TaxiRide taxiRide = taxiRideRepository.findByRequestId(requestId).orElseThrow(
                () -> new RuntimeException("Servicio no encontrado")
        );
        if (taxiRide.getStatus() != ServiceStatus.ACCEPTED) {
            throw new IllegalArgumentException("El Conductor Viene en Camino!!!");
        }
        if (!taxiRide.getRiderId().equals(riderId)) {
            throw new IllegalArgumentException("No puedes Cancelar solicitudes de Otros");
        } else {
            taxiRide.setStatus(ServiceStatus.CANCELLED);
            taxiRide.setCancelledAt(LocalDateTime.now());
            taxiRide.setUpdatedAt(LocalDateTime.now());
            TaxiRide updatedEntity = taxiRideRepository.save(taxiRide);

            TaxiRideRequest updatedRequest = updatedEntity.toTaxiServiceRequest();
            activeRequests.remove(requestId);
            riderToRequestMap.remove(riderId);
            if (updatedRequest.getDriverId() != null) {
                driverToRequestMap.remove(updatedRequest.getDriverId());
            }
            log.info("🚫 Servicio cancelado en BD - Request: {}, Rider: {}", requestId, riderId);
            return updatedRequest;
        }
    }

    @Override
    @Transactional
    public TaxiRideRequest completeServiceRequest(String requestId, Long driverId, Double finalPrice) {
        TaxiRide taxiRide = taxiRideRepository.findByRequestId(requestId).orElseThrow(
                () -> new RuntimeException("Servicio no encontrado")
        );
        if (taxiRide.getDriverId() != null && taxiRide.getDriverId().equals(driverId) &&
                (taxiRide.getStatus() == ServiceStatus.ACCEPTED || taxiRide.getStatus() == ServiceStatus.IN_PROGRESS)) {
            taxiRide.setStatus(ServiceStatus.COMPLETED);
            taxiRide.setFinalPrice(finalPrice);
            taxiRide.setCompletedAt(LocalDateTime.now());
            taxiRide.setUpdatedAt(LocalDateTime.now());
            TaxiRide updatedEntity = taxiRideRepository.save(taxiRide);
            TaxiRideRequest updatedRequest = updatedEntity.toTaxiServiceRequest();
            activeRequests.remove(requestId);
            riderToRequestMap.remove(updatedRequest.getRiderId());
            driverToRequestMap.remove(driverId);
            log.info("🎉 Servicio completado en BD - Request: {}, Driver: {}, Precio: {}",
                    requestId, driverId, finalPrice);
            return updatedRequest;
        } else  {
            throw new IllegalArgumentException("No se pudo Actualizar tu solicitud!!!");
        }
    }

    @Override
    @Transactional
    public TaxiRideRequest startService(String requestId, Long driverId) {
        TaxiRide taxiRide = taxiRideRepository.findByRequestId(requestId).orElseThrow(
                () -> new RuntimeException("Servicio no encontrado")
        );
        if (taxiRide.getDriverId() != null && taxiRide.getDriverId().equals(driverId) &&
                taxiRide.getStatus() == ServiceStatus.ACCEPTED) {
            taxiRide.setStatus(ServiceStatus.IN_PROGRESS);
            taxiRide.setStartedAt(LocalDateTime.now());
            taxiRide.setUpdatedAt(LocalDateTime.now());
            TaxiRide updatedEntity = taxiRideRepository.save(taxiRide);
            TaxiRideRequest updatedRequest = updatedEntity.toTaxiServiceRequest();
            activeRequests.put(requestId, updatedRequest);
            log.info("🚗 Servicio iniciado en BD - Request: {}", requestId);
            return updatedRequest;
        } else {
            throw new IllegalArgumentException("No se pudo Actualizar tu solicitud!!!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void loadActiveServicesToCache() {
        List<ServiceStatus> activeStatuses = List.of(ServiceStatus.PENDING, ServiceStatus.ACCEPTED, ServiceStatus.IN_PROGRESS);
        for (ServiceStatus status : activeStatuses) {
            List<TaxiRide> activeServices = taxiRideRepository.findByStatusOrderByRequestedAtAsc(status);
            for (TaxiRide entity : activeServices) {
                TaxiRideRequest request = entity.toTaxiServiceRequest();
                activeRequests.put(request.getRequestId(), request);
                if (entity.getRiderId() != null) {
                    riderToRequestMap.put(entity.getRiderId(), request.getRequestId());
                }
                if (entity.getDriverId() != null) {
                    driverToRequestMap.put(entity.getDriverId(), request.getRequestId());
                }
            }
        }
        log.info("📦 Cargados {} servicios activos desde BD al cache", activeRequests.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaxiRideRequest> getPendingRequests() {
        return taxiRideRepository.findByStatusOrderByRequestedAtAsc(ServiceStatus.PENDING)
                .stream()
                .map(TaxiRide::toTaxiServiceRequest)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaxiRideRequest getRiderActiveRequest(Long riderId) {
        String requestId = riderToRequestMap.get(riderId);
        if (requestId != null) {
            return activeRequests.get(requestId);
        }
        return taxiRideRepository.findActiveServiceByRider(riderId)
                .map(TaxiRide::toTaxiServiceRequest)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public TaxiRideRequest getDriverActiveRequest(Long driverId) {
        String requestId = driverToRequestMap.get(driverId);
        if (requestId != null) {
            return activeRequests.get(requestId);
        }
        return taxiRideRepository.findActiveServiceByDriver(driverId)
                .map(TaxiRide::toTaxiServiceRequest)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaxiRideRequest> getRiderHistory(Long riderId) {
        return taxiRideRepository.findByRiderIdOrderByRequestedAtDesc(riderId)
                .stream()
                .map(TaxiRide::toTaxiServiceRequest)
                .toList();
    }

    @Override
    public List<TaxiRideRequest> getDriverHistory(Long driverId) {
        return taxiRideRepository.findByDriverIdOrderByRequestedAtDesc(driverId)
                .stream()
                .map(TaxiRide::toTaxiServiceRequest)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaxiRideRequest> getAllServices(Pageable pageable) {
        return taxiRideRepository.findAllByOrderByRequestedAtDesc(pageable)
                .map(TaxiRide::toTaxiServiceRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllServices() {
        return taxiRideRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> countServiceByStatus() {
        Map<String, Object> stats = new HashMap<>();
        List<TaxiRide> totalServices = taxiRideRepository.findAll();
        stats.put("total", (long) totalServices.size());
        stats.put("pending", totalServices.stream().filter(st -> st.getStatus().equals(ServiceStatus.PENDING)).count());
        stats.put("accepted", totalServices.stream().filter(st -> st.getStatus().equals(ServiceStatus.ACCEPTED)).count());
        stats.put("inProgress", totalServices.stream().filter(st -> st.getStatus().equals(ServiceStatus.IN_PROGRESS)).count());
        stats.put("completed", totalServices.stream().filter(st -> st.getStatus().equals(ServiceStatus.COMPLETED)).count());
        stats.put("cancelled", totalServices.stream().filter(st -> st.getStatus().equals(ServiceStatus.CANCELLED)).count());
        stats.put("rejected", totalServices.stream().filter(st -> st.getStatus().equals(ServiceStatus.REJECTED)).count());
        return stats;

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaxiRideRequest> getServiceByRequestId(String requestId) {
        return taxiRideRepository.findByRequestId(requestId)
                .map(TaxiRide::toTaxiServiceRequest);
    }

    @Override
    public PriceResponse getEstimatedPrice(PriceRequest request) {
        if (request.getPickupLat() == null || request.getPickupLng() == null ||
                request.getDestinationLat() == null || request.getDestinationLng() == null) {
            throw new IllegalArgumentException("Se requieren coordenadas de recogida y destino");
        }

        Map<String, Object> fareResponse = fareCalculationService.calculateFareFromCoordinates(request);
        return PriceResponse.builder()
                .estimatedUsdPrice((Double) fareResponse.get("estimatedUsd"))
                .subtotalBs((Double) fareResponse.get("subtotalBs"))
                .taxIva((Double) fareResponse.get("taxIva"))
                .amountIva((Double) fareResponse.get("amountIva"))
                .estimatedBsPrice((Double) fareResponse.get("estimatedBs"))
                .distance((Double) fareResponse.get("distanceKm"))
                .build();
    }
}
