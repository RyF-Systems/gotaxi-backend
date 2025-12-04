package com.ryfsystems.ryftaxi.model;

import com.ryfsystems.ryftaxi.dto.Phone;
import com.ryfsystems.ryftaxi.utils.PhoneListConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Entity(name = "business_configuration")
public class BusinessConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 50, message = "El name debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Size(min = 12, max = 12, message = "El rif debe tener 12 caracteres")
    @Column(unique = true, nullable = false, length = 12)
    private String rif;

    private Double basePriceUsd;
    private Double fractionPriceUsd;
    private Double taxIva;

    @Convert(converter = PhoneListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Phone> phoneList;

}
