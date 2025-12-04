package com.ryfsystems.ryftaxi.service;

import com.ryfsystems.ryftaxi.dto.DolarApiResponse;
import com.ryfsystems.ryftaxi.model.ExchangeRates;

import java.time.LocalDate;

public interface ExchangeRatesService {
    ExchangeRates findByDate(LocalDate date);

    DolarApiResponse getOfficialRate();

    ExchangeRates updateOfficialRate();
}
