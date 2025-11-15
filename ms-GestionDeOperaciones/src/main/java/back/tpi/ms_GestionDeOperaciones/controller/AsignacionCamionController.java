package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.AsignacionCamionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Asignación de Camiones",
        description = "Operaciones para asignar, consultar y liberar camiones asociados a tramos"
)
@RestController
@RequestMapping("/api/asignacion-camiones")
@RequiredArgsConstructor
public class AsignacionCamionController {

    private final AsignacionCamionService service;

    // ============================================================
    // 1) ASIGNAR CAMIÓN A TRAMO
    // ============================================================

    @Operation(
            summary = "Asigna un camión a un tramo",
            description = "Permite asignar un camión disponible a un tramo, validando capacidad y compatibilidad.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de asignación del camión al tramo",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AsignarCamionDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo asignación",
                                    value = """
                                            {
                                                "tramoId": 12,
                                                "camionId": 4
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Camión asignado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en la validación o en la asignación",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado — requiere rol ADMINISTRADOR"),
    })
    @PostMapping("/asignar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> asignarCamionATramo(@RequestBody AsignarCamionDTO asignacionDTO) {
        try {
            TramoDTO tramoActualizado = service.asignarCamionATramo(asignacionDTO);
            return ResponseEntity.ok(tramoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }


    // ============================================================
    // 2) OBTENER CAMIONES DISPONIBLES PARA UN CONTENEDOR
    // ============================================================

    @Operation(
            summary = "Consulta camiones disponibles para un contenedor",
            description = "Devuelve camiones que cumplen con el peso y volumen del contenedor."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron camiones disponibles"),
            @ApiResponse(responseCode = "403", description = "No autorizado — requiere rol ADMINISTRADOR"),
    })
    @GetMapping("/disponibles/contenedor/{contenedorId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<CamionDTO>> obtenerCamionesDisponiblesParaContenedor(
            @Parameter(description = "ID del contenedor", example = "15")
            @PathVariable Long contenedorId) {

        try {
            List<CamionDTO> camiones = service.obtenerCamionesDisponiblesParaContenedor(contenedorId);
            return ResponseEntity.ok(camiones);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // ============================================================
    // 3) OBTENER CAMIONES DISPONIBLES PARA UN TRAMO
    // ============================================================

    @Operation(
            summary = "Consulta camiones disponibles para un tramo",
            description = "Devuelve los camiones aptos para el tramo indicado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron camiones disponibles"),
            @ApiResponse(responseCode = "403", description = "No autorizado — requiere rol ADMINISTRADOR"),
    })
    @GetMapping("/disponibles/tramo/{tramoId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<CamionDTO>> obtenerCamionesDisponiblesParaTramo(
            @Parameter(description = "ID del tramo", example = "8")
            @PathVariable Long tramoId) {

        try {
            List<CamionDTO> camiones = service.obtenerCamionesDisponiblesParaTramo(tramoId);
            return ResponseEntity.ok(camiones);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // ============================================================
    // 4) LIBERAR CAMIÓN DE UN TRAMO
    // ============================================================

    @Operation(
            summary = "Libera el camión asignado a un tramo",
            description = "Quita la asociación entre un tramo y su camión."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Camión liberado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al liberar",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado — requiere rol ADMINISTRADOR"),
    })
    @DeleteMapping("/tramo/{tramoId}/camion")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> liberarCamionDeTramo(
            @Parameter(description = "ID del tramo", example = "5")
            @PathVariable Long tramoId) {

        try {
            TramoDTO tramoActualizado = service.liberarCamionDeTramo(tramoId);
            return ResponseEntity.ok(tramoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }


    // ============================================================
    // 5) DTO PARA ERRORES
    // ============================================================

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ErrorResponse {

        @Schema(description = "Mensaje que describe el error ocurrido", example = "El camión no tiene capacidad suficiente")
        private String mensaje;
    }
}