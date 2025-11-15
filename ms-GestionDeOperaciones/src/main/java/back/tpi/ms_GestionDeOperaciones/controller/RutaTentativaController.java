package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.RutaTentativaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas-tentativas")
@RequiredArgsConstructor
@Tag(
        name = "Rutas Tentativas",
        description = "Generación y confirmación de rutas tentativas con diferentes estrategias"
)
@SecurityRequirement(name = "bearer-jwt")
@SecurityRequirement(name = "keycloak-oauth2")
public class RutaTentativaController {

    private final RutaTentativaService rutaTentativaService;

    // =========================================================================
    // CONSULTAR RUTAS TENTATIVAS
    // =========================================================================
    @Operation(
            summary = "Consultar rutas tentativas",
            description = """
                    Genera hasta 3 opciones de rutas tentativas para una solicitud:

                    • **Opción 1** → Ruta directa sin depósitos
                    • **Opción 2** → Ruta con 1 depósito  
                    • **Opción 3** → Ruta con 2 depósitos  

                    Cada opción incluye distancia, costo estimado, tiempo y tramos.

                    **Roles permitidos:** ADMINISTRADOR, CLIENTE
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rutas generadas correctamente",
                    content = @Content(schema = @Schema(implementation = RutaTentativaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud no está en estado APROBADA"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    @GetMapping("/solicitud/{solicitudId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CLIENTE')")
    public ResponseEntity<List<RutaTentativaDTO>> consultarRutasTentativas(
            @Parameter(
                    description = "ID de la solicitud de traslado",
                    required = true,
                    example = "5"
            )
            @PathVariable Long solicitudId
    ) {
        try {
            List<RutaTentativaDTO> rutas = rutaTentativaService
                    .consultarRutasTentativas(solicitudId);

            return ResponseEntity.ok(rutas);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // =========================================================================
    // CONFIRMAR UNA RUTA TENTATIVA
    // =========================================================================
    @Operation(
            summary = "Confirmar una ruta tentativa",
            description = """
                    Confirma una de las rutas tentativas generadas previamente.

                    **Proceso interno:**
                    1. Validación de que la solicitud no tenga ruta asignada  
                    2. Se crea la entidad Ruta con sus tramos  
                    3. Se actualizan los datos estimados en la solicitud  
                    4. Los tramos quedan en estado **PENDIENTE**

                    **Rol requerido:** ADMINISTRADOR
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para seleccionar una ruta tentativa",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ConfirmarRutaTentativaDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ruta confirmada correctamente",
                    content = @Content(schema = @Schema(implementation = RutaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la confirmación (opción inválida o solicitud ya tiene ruta)"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "403", description = "Debe ser ADMINISTRADOR")
    })
    @PostMapping("/confirmar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RutaDTO> confirmarRutaTentativa(
            @RequestBody ConfirmarRutaTentativaDTO confirmarDTO
    ) {
        try {
            RutaDTO ruta = rutaTentativaService.confirmarRutaTentativa(
                    confirmarDTO.getSolicitudTrasladoId(),
                    confirmarDTO.getNumeroOpcionSeleccionada()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(ruta);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
