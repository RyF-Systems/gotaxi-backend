package com.ryfsystems.ryftaxi.controller;

import com.ryfsystems.ryftaxi.dto.TaxiRideRequest;
import com.ryfsystems.ryftaxi.service.TaxiRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class TaxiRideController {

    private final TaxiRideService taxiRideService;

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<TaxiRideRequest>> getRiderServices(@PathVariable Long riderId) {
        List<TaxiRideRequest> services = taxiRideService.getRiderHistory(riderId);
        return ResponseEntity.ok(services);
    }
}
