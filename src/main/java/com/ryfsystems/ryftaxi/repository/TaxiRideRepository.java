package com.ryfsystems.ryftaxi.repository;

import com.ryfsystems.ryftaxi.model.TaxiRide;
import com.ryfsystems.ryftaxi.enums.ServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaxiRideRepository extends JpaRepository<TaxiRide, Long> {

    Optional<TaxiRide> findByRequestId(String requestId);

    List<TaxiRide> findByStatusOrderByRequestedAtAsc(ServiceStatus status);

    List<TaxiRide> findByRiderIdOrderByRequestedAtDesc(Long id);

    List<TaxiRide> findByDriverIdOrderByRequestedAtDesc(Long driverId);

    List<TaxiRide> findByRiderIdAndStatus(Long id, ServiceStatus status);

    List<TaxiRide> findByDriverIdAndStatus(Long driverId, ServiceStatus status);

    @Query("SELECT t FROM taxi_rides t WHERE t.status = 'PENDING' AND t.requestedAt >= :since ORDER BY t.requestedAt ASC")
    List<TaxiRide> findPendingRequestsSince(@Param("since") LocalDateTime since);

    @Query("SELECT t FROM taxi_rides t WHERE t.requestedAt BETWEEN :startDate AND :endDate ORDER BY t.requestedAt DESC")
    List<TaxiRide> findByRequestDateRange(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM taxi_rides t WHERE t.status = :status")
    long countByStatus(@Param("status") ServiceStatus status);

    @Query("SELECT t FROM taxi_rides t WHERE t.riderId = :riderId AND t.status IN ('PENDING', 'ACCEPTED', 'IN_PROGRESS')")
    Optional<TaxiRide> findActiveServiceByRider(@Param("riderId") Long riderId);

    @Query("SELECT t FROM taxi_rides t WHERE t.driverId = :driverId AND t.status IN ('ACCEPTED', 'IN_PROGRESS')")
    Optional<TaxiRide> findActiveServiceByDriver(@Param("driverId") Long driverId);

    Page<TaxiRide> findAllByOrderByRequestedAtDesc(Pageable pageable);

    @Query("SELECT t FROM taxi_rides t WHERE " +
            "(:riderId IS NULL OR t.riderId = :riderId) AND " +
            "(:driverId IS NULL OR t.driverId = :driverId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "t.requestedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY t.requestedAt DESC")
    Page<TaxiRide> findWithFilters(@Param("riderId") String riderId,
                                   @Param("driverId") String driverId,
                                   @Param("status") ServiceStatus status,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);

}
