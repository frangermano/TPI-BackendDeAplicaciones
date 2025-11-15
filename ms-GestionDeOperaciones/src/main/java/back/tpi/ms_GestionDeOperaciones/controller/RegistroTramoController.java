package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.RegistroTramoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Registro tramos", description = "Operaciones para registrar inicio y fin de un tramo, y consultar su estado")
@RestController
@RequestMapping("/api/registro-tramos")
@RequiredArgsConstructor
public class RegistroTramoController {

    private final RegistroTramoService service;

    // ============================================================
    // INICIAR TRAMO
    // ============================================================
    @Operation(
            summary = "Registrar inicio de un tramo",
            description = """
                    Permite que un transportista registre el inicio de un tramo.
                    Debe indicar:
                    - ID del tramo
                    - ID del camión asignado
                    - Fecha/hora de inicio
                    
                    POST /api/registro-tramos/iniciar
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inicio de tramo registrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistroTramoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o tramo no válido",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/iniciar")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<?> iniciarTramo(
            @RequestBody
            @Parameter(description = "Datos necesarios para registrar inicio de tramo")
            RegistroTramoDTO registroDTO) {

        try {
            RegistroTramoResponseDTO response = service.registrarInicioTramo(registroDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }


    // ============================================================
    //  FINALIZAR TRAMO
    // ============================================================
    @Operation(
            summary = "Registrar fin de un tramo",
            description = """
                    Permite que un transportista registre la finalización de un tramo.
                    Debe indicar:
                    - ID del tramo
                    - Fecha/hora final
                    
                    POST /api/registro-tramos/finalizar
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fin de tramo registrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistroTramoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o tramo no válido",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/finalizar")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<?> finalizarTramo(
            @RequestBody
            @Parameter(description = "Datos necesarios para registrar fin de tramo")
            RegistroTramoDTO registroDTO) {

        try {
            RegistroTramoResponseDTO response = service.registrarFinTramo(registroDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }


    // ============================================================
    //  CONSULTAR ESTADO DE UN TRAMO
    // ============================================================
    @Operation(
            summary = "Obtener estado actual de un tramo",
            description = """
                    Devuelve el estado del tramo:
                    - En curso
                    - No iniciado
                    - Finalizado
                    - Datos del camión
                    - Horarios
                    
                    GET /api/registro-tramos/{tramoId}
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TramoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tramo no encontrado")
    })
    @GetMapping("/{tramoId}")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<TramoDTO> obtenerEstadoTramo(
            @Parameter(description = "ID del tramo a consultar", example = "12")
            @PathVariable Long tramoId) {

        try {
            TramoDTO tramo = service.obtenerEstadoTramo(tramoId);
            return ResponseEntity.ok(tramo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // ============================================================
    // DTO de ERROR
    // ============================================================
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ErrorResponse {
        private String mensaje;
    }
}