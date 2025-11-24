package com.ryfsystems.ryftaxi.model;

import jakarta.persistence.*;
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
    @Column(unique = true)
    private String plate;

    @Override
    public String toString() {
        return brand + ' ' + model + ' ' + color + '-' + year + ". Placa: " + plate;
    }
}
