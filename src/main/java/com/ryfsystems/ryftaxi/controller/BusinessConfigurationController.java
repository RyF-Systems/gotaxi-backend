package com.ryfsystems.ryftaxi.controller;

import com.ryfsystems.ryftaxi.dto.BusinessConfigurationDTO;
import com.ryfsystems.ryftaxi.model.BusinessConfiguration;
import com.ryfsystems.ryftaxi.service.BusinessConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "🚖 BusinessConfigurationController", description = "Gestión de la Empresa")
public class BusinessConfigurationController {

    private final BusinessConfigurationService businessConfigurationService;

    @Operation(
            summary = "Obtener los Datos de la Empresa",
            description = "Obtener todos los datos de la Empresa"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitud de Datos Generada"
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

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "Actualización parcial de configuración",
            description = "Actualiza solo los campos especificados de una configuración de negocio"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflicto: nombre o RIF ya existen")
    })
    public ResponseEntity<BusinessConfigurationDTO> update(@PathVariable Long id,
                                                           @Valid @RequestBody BusinessConfigurationDTO patchDTO
    ) {
        try {
            BusinessConfigurationDTO updatedConfig = businessConfigurationService.update(id, patchDTO);
            return ResponseEntity.ok(updatedConfig);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Ya existe")) {
                return ResponseEntity.status(409).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}
