package com.ryfsystems.ryftaxi.service.impl;

import com.ryfsystems.ryftaxi.dto.DolarApiResponse;
import com.ryfsystems.ryftaxi.model.ExchangeRates;
import com.ryfsystems.ryftaxi.repository.ExchangeRatesRepository;
import com.ryfsystems.ryftaxi.service.DolarApiService;
import com.ryfsystems.ryftaxi.service.ExchangeRatesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private final ExchangeRatesRepository exchangeRatesRepository;
    private final DolarApiService dolarApiService;

    @Override
    public ExchangeRates findByDate(LocalDate date) {
        return exchangeRatesRepository.findByEnabledAndDate(true, date);
    }

    @Override
    public DolarApiResponse getOfficialRate() {
        return dolarApiService.getOfficialRate();
    }

    @Override
    public ExchangeRates updateOfficialRate() {
        ExchangeRates localRate = this.findByDate(LocalDate.now());
        ExchangeRates currentRate = convertToExchangeRate(this.getOfficialRate());
        ExchangeRates newExchangeRate;
        if (localRate != null) {
            if (!currentRate.getAmountRate().equals(localRate.getAmountRate())) {
                localRate.setEnabled(false);
                newExchangeRate = exchangeRatesRepository.save(new ExchangeRates(currentRate.getAmountRate()));
                exchangeRatesRepository.save(localRate);
            } else {
                return localRate;
            }
        } else {
            newExchangeRate = exchangeRatesRepository.save(new ExchangeRates(currentRate.getAmountRate()));
        }
        return newExchangeRate;
    }

    private ExchangeRates convertToExchangeRate(DolarApiResponse apiResponse) {
        ExchangeRates exchangeRate = new ExchangeRates();
        exchangeRate.setAmountRate(apiResponse.getPromedio());

        if (apiResponse.getFechaActualizacion() != null) {
            exchangeRate.setDate(apiResponse.getFechaActualizacion().toLocalDate());
        } else {
            exchangeRate.setDate(LocalDate.now());
        }
        exchangeRate.setUpdateTime(LocalDateTime.now());
        return exchangeRate;
    }
}
