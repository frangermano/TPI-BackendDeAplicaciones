package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.ContenedorPendienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Contenedores pendientes", description = "Operaciones para consultar contenedores pendientes de entrega")
@RestController
@RequestMapping("/api/contenedores-pendientes")
@RequiredArgsConstructor
public class ContenedorPendienteController {

    private final ContenedorPendienteService service;

    @Operation(
            summary = "Consultar contenedores pendientes con filtros opcionales",
            description = """
                    Permite obtener una lista de contenedores pendientes aplicando diferentes filtros.
                    
                    Ejemplos de uso:
                    - /api/contenedores-pendientes
                    - /api/contenedores-pendientes?estado=EN_PROCESO
                    - /api/contenedores-pendientes?clienteId=5
                    - /api/contenedores-pendientes?pesoMinimo=100&pesoMaximo=500
                    - /api/contenedores-pendientes?ciudadOrigen=Rosario&ciudadDestino=Córdoba
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContenedorPendienteDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos o error en la consulta")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ContenedorPendienteDTO>> consultarContenedoresPendientes(

            @Parameter(description = "Estado de la solicitud (Ej: PENDIENTE, EN_PROCESO, COMPLETADA)")
            @RequestParam(required = false) String estado,

            @Parameter(description = "ID del cliente asociado")
            @RequestParam(required = false) Long clienteId,

            @Parameter(description = "Fecha mínima de solicitud (ISO-8601)", example = "2025-01-20T10:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaSolicitudDesde,

            @Parameter(description = "Fecha máxima de solicitud (ISO-8601)", example = "2025-02-10T18:30:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaSolicitudHasta,

            @Parameter(description = "Peso mínimo del contenedor (kg)")
            @RequestParam(required = false) Double pesoMinimo,

            @Parameter(description = "Peso máximo del contenedor (kg)")
            @RequestParam(required = false) Double pesoMaximo,

            @Parameter(description = "Volumen mínimo del contenedor (m³)")
            @RequestParam(required = false) Double volumenMinimo,

            @Parameter(description = "Volumen máximo del contenedor (m³)")
            @RequestParam(required = false) Double volumenMaximo,

            @Parameter(description = "Ciudad de origen del traslado")
            @RequestParam(required = false) String ciudadOrigen,

            @Parameter(description = "Ciudad de destino del traslado")
            @RequestParam(required = false) String ciudadDestino
    ) {

        try {
            FiltrosContenedorDTO filtros = FiltrosContenedorDTO.builder()
                    .estado(estado != null ? EstadoSolicitud.valueOf(estado) : null)
                    .clienteId(clienteId)
                    .fechaSolicitudDesde(fechaSolicitudDesde)
                    .fechaSolicitudHasta(fechaSolicitudHasta)
                    .pesoMinimo(pesoMinimo)
                    .pesoMaximo(pesoMaximo)
                    .volumenMinimo(volumenMinimo)
                    .volumenMaximo(volumenMaximo)
                    .ciudadOrigen(ciudadOrigen)
                    .ciudadDestino(ciudadDestino)
                    .build();

            List<ContenedorPendienteDTO> contenedores =
                    service.consultarContenedoresPendientes(filtros);

            return ResponseEntity.ok(contenedores);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}