package com.ryfsystems.ryftaxi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity(name = "exchange_rates")
@NoArgsConstructor
public class ExchangeRates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Double amountRate;
    private LocalDateTime updateTime;
    private Boolean enabled;

    public ExchangeRates(Double amountRate) {
        this.date = LocalDate.now();
        this.amountRate = amountRate;
        this.updateTime = LocalDateTime.now();
        this.enabled = true;
    }
}
