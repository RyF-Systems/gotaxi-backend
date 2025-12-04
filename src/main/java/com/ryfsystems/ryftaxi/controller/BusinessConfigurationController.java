package com.ryfsystems.ryftaxi.controller;

import com.ryfsystems.ryftaxi.model.BusinessConfiguration;
import com.ryfsystems.ryftaxi.service.BusinessConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "🚖 BusinessConfigurationController", description = "Gestión completa de la Empresa")
public class BusinessConfigurationController {

    private final BusinessConfigurationService businessConfigurationService;

    @Operation(
            summary = "Obtener los Datos de la Empresa",
            description = "Obtener todos los datos de la Empresa"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitud de Datos Actualizada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida",
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
    @GetMapping("/{id}")
    public BusinessConfiguration getById(@PathVariable Long id) {
        return businessConfigurationService.findById(id);
    }
}
