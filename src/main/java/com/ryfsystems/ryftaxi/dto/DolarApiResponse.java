package com.ryfsystems.ryftaxi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DolarApiResponse {

    @JsonProperty("fechaActualizacion")
    private LocalDateTime fechaActualizacion;

    @JsonProperty("promedio")
    private Double promedio;
}
