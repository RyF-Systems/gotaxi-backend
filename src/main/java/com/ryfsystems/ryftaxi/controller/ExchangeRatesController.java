package com.ryfsystems.ryftaxi.controller;

import com.ryfsystems.ryftaxi.dto.DolarApiResponse;
import com.ryfsystems.ryftaxi.model.ExchangeRates;
import com.ryfsystems.ryftaxi.service.ExchangeRatesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchange-rates")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "🚖 ExchangeController", description = "Gestión de Tasas de Intercambio")
public class ExchangeRatesController {

    private final ExchangeRatesService exchangeRatesService;

    @Operation(
            summary = "Obtener la Tasa del Día",
            description = "Obtener la tasa de Intercambio para una fecha específica"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasa de cambio encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fecha inválida o formato incorrecto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró tasa para la fecha especificada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado - Token inválido o expirado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/by-date/{date}")
    public ResponseEntity<ExchangeRates> getByDate(
            @Parameter(
                    description = "Fecha en formato YYYY-MM-DD (ej: 2024-01-15)",
                    example = "2025-12-04",
                    required = true
            )
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return ResponseEntity.ok(exchangeRatesService.findByDate(date));
    }

    @Operation(
            summary = "Obtener tasa oficial actual",
            description = "Obtiene la tasa de cambio oficial del dólar del día actual desde DolarAPI"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasa obtenida exitosamente"
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Error al conectar con la API externa",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Servicio temporalmente no disponible",
                    content = @Content(
                            mediaType = "application/json"
                    )
            )
    })
    @GetMapping("/official/current")
    public ResponseEntity<DolarApiResponse> getCurrentOfficialRate() {
        DolarApiResponse rate = exchangeRatesService.getOfficialRate();
        return ResponseEntity.ok(rate);
    }

    @Operation(
            summary = "Actualizar tasa oficial actual",
            description = "Actualiza la tasa de cambio oficial del dólar del día actual desde DolarAPI"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasa Actualizada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Error al conectar con la API externa",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Servicio temporalmente no disponible",
                    content = @Content(
                            mediaType = "application/json"
                    )
            )
    })
    @PostMapping("/official/current")
    public ResponseEntity<ExchangeRates> updateCurrentOfficialRate() {
        ExchangeRates rate = exchangeRatesService.updateOfficialRate();
        return ResponseEntity.ok(rate);
    }
}
