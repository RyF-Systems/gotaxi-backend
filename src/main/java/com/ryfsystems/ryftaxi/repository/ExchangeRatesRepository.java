package com.ryfsystems.ryftaxi.repository;

import com.ryfsystems.ryftaxi.model.ExchangeRates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ExchangeRatesRepository extends JpaRepository<ExchangeRates, Long> {

    ExchangeRates findByEnabledAndDate(Boolean enabled, LocalDate date);

}
