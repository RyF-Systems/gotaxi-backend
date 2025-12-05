package com.ryfsystems.ryftaxi.controller;

import com.ryfsystems.ryftaxi.dto.PriceRequest;
import com.ryfsystems.ryftaxi.dto.PriceResponse;
import com.ryfsystems.ryftaxi.dto.TaxiRideRequest;
import com.ryfsystems.ryftaxi.service.TaxiRideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@Tag(name = "🚖 TaxiRideController", description = "Gestión completa de servicios de taxi")
@SecurityRequirement(name = "bearerAuth")
public class TaxiRideController {

    private final TaxiRideService taxiRideService;

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<TaxiRideRequest>> getRiderServices(@PathVariable Long riderId) {
        List<TaxiRideRequest> services = taxiRideService.getRiderHistory(riderId);
        return ResponseEntity.ok(services);
    }

    @Operation(
            summary = "Crear nueva solicitud de servicio",
            description = """
            Crea una nueva solicitud de taxi. 
            
            **Requisitos:**
            - Solo usuarios con rol RIDER pueden crear solicitudes
            - El usuario no debe tener servicios activos pendientes
            - Se requiere autenticación con token JWT
            
            **Flujo:**
            1. El RIDER solicita un taxi con ubicación de recogida y destino
            2. La solicitud se guarda en base de datos con estado PENDING
            3. Se notifica a todos los DRIVERS disponibles via WebSocket
            4. Un DRIVER puede aceptar la solicitud
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Solicitud creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaxiRideRequest.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Usuario no es RIDER",
                                            value = """
                            {
                              "timestamp": "2023-12-07T10:30:00",
                              "status": 400,
                              "error": "Bad Request",
                              "message": "Solo los usuarios tipo rider pueden solicitar servicios",
                              "path": "/api/taxi-services/request"
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "Servicio activo existente",
                                            value = """
                            {
                              "timestamp": "2023-12-07T10:30:00",
                              "status": 400,
                              "error": "Bad Request",
                              "message": "Ya tienes un servicio activo",
                              "path": "/api/taxi-services/request"
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado - Token inválido o expirado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Prohibido - Usuario no tiene permisos de RIDER",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/request")
    public ResponseEntity<TaxiRideRequest> createServiceRequest(
            @Parameter(
                    description = "Datos de la solicitud de taxi",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TaxiRideRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Solicitud básica",
                                            value = """
                                {
                                  "pickupAddress": "Av. Principal #123, Ciudad",
                                  "destinationAddress": "Centro Comercial Plaza, Zona Norte",
                                  "pickupLat": 4.710989,
                                  "pickupLng": -74.072092,
                                  "destinationLat": 4.698070,
                                  "destinationLng": -74.056595,
                                  "estimatedPrice": 15000.0
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Solicitud con notas",
                                            value = """
                                {
                                  "pickupAddress": "Carrera 45 #26-85",
                                  "destinationAddress": "Aeropuerto Internacional",
                                  "pickupLat": 4.701234,
                                  "pickupLng": -74.065432,
                                  "destinationLat": 4.712345,
                                  "destinationLng": -74.043210,
                                  "estimatedPrice": 35000.0,
                                  "notes": "Por favor, traiga espacio para 2 maletas grandes"
                                }
                                """
                                    )
                            }
                    )
            )
            @Valid @RequestBody TaxiRideRequest request) {

        // Llamar al servicio para crear la solicitud
        TaxiRideRequest createdRequest = taxiRideService.createServiceRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRequest);
    }

    @PostMapping("/get-price")
    @PreAuthorize("hasAuthority('ROLE_RIDER')")
    public ResponseEntity<PriceResponse> getPrice(@Valid @RequestBody PriceRequest priceRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taxiRideService.getEstimatedPrice(priceRequest));
    }
}
