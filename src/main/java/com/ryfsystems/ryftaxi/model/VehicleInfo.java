package com.ryfsystems.ryftaxi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "vehicle_info")
public class VehicleInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String brand;
    private String model;
    private String color;
    private Long year;
    private String plate;

    @Override
    public String toString() {
        return brand + ',' + model + ' '
                + color + '-' + year + ". " + plate;
    }
}
