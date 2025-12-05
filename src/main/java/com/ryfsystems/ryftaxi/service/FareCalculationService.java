package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.dto.PriceRequest;

import java.util.Map;

public interface FareCalculationService {

    Map<String, Object> calculateFareFromCoordinates(PriceRequest request);
}
