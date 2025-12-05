package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.PriceRequest;
import com.ryfsystems.ryftaxi.model.BusinessConfiguration;
import com.ryfsystems.ryftaxi.model.ExchangeRates;
import com.ryfsystems.ryftaxi.service.BusinessConfigurationService;
import com.ryfsystems.ryftaxi.service.ExchangeRatesService;
import com.ryfsystems.ryftaxi.service.FareCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FareCalculationServiceImpl implements FareCalculationService {

    private final ExchangeRatesService exchangeRatesService;
    private final BusinessConfigurationService businessConfigurationService;

    @Override
    public Map<String, Object> calculateFareFromCoordinates(PriceRequest request) {
        BusinessConfiguration bc = businessConfigurationService.findById(1L);
        double distance = calculateDistance(request.getPickupLat(), request.getPickupLng(),
                request.getDestinationLat(), request.getDestinationLng());
        double baseFare = calculateFare(distance, bc);
        Map<String, Object> fareDetails = calculateFareWithTaxes(baseFare, bc);
        fareDetails.put("estimatedUsd", baseFare);
        fareDetails.put("distanceKm", distance);

        return fareDetails;
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        distance = Math.round(distance * 100.0) / 100.0;

        return distance;
    }

    public double calculateFare(double distanceKm, BusinessConfiguration bc) {
        double baseFare1ToBonus = bc.getBasePriceUsd();
        double costPerKmAfterBonus = bc.getFractionPriceUsd();
        double fare;

        if (distanceKm <= bc.getMinDistanceBonus()) {
            fare = baseFare1ToBonus;
        } else {
            double additionalKm = distanceKm - bc.getMinDistanceBonus();
            fare = baseFare1ToBonus + (additionalKm * costPerKmAfterBonus);
        }

        return fare;
    }

    public Map<String, Object> calculateFareWithTaxes(double baseFare, BusinessConfiguration bc) {
        ExchangeRates er = exchangeRatesService.updateOfficialRate();

        double bsFare = baseFare * er.getAmountRate();
        double taxPercentage = bc.getTaxIva();
        // Calcular impuestos
        double tax = (bsFare * taxPercentage) / 100.0;
        // Calcular total
        double total = bsFare + tax;

        // Redondear a múltiplos de 100
        total = Math.round(total / 100.0) * 100.0;
        bsFare = Math.round(bsFare / 100.0) * 100.0;
        tax = Math.round(tax / 100.0) * 100.0;

        Map<String, Object> fareDetails = new java.util.HashMap<>();
        fareDetails.put("subtotalBs", bsFare);
        fareDetails.put("amountIva", tax);
        fareDetails.put("taxIva", taxPercentage);
        fareDetails.put("estimatedBs", total);
        fareDetails.put("distanceKm", 0.0); // Se establecerá después
        return fareDetails;
    }
}
