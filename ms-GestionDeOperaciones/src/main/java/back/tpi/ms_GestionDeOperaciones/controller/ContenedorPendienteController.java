package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.ContenedorPendienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/contenedores-pendientes")
@RequiredArgsConstructor
public class ContenedorPendienteController {

    private final ContenedorPendienteService service;

    /**
     * Consulta todos los contenedores pendientes con filtros opcionales
     * GET /api/contenedores-pendientes
     */
    @GetMapping
    public ResponseEntity<ConsultaContenedoresResponseDTO> consultarContenedoresPendientes(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String clienteNombre,
            @RequestParam(required = false) String estadoSolicitud,
            @RequestParam(required = false) String estadoTramo,
            @RequestParam(required = false) Double pesoMinimo,
            @RequestParam(required = false) Double pesoMaximo,
            @RequestParam(required = false) Double volumenMinimo,
            @RequestParam(required = false) Double volumenMaximo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaSolicitudDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaSolicitudHasta,
            @RequestParam(required = false) String ciudadOrigen,
            @RequestParam(required = false) String ciudadDestino,
            @RequestParam(required = false) Double costoMinimo,
            @RequestParam(required = false) Double costoMaximo,
            @RequestParam(required = false) Boolean soloAtrasados,
            @RequestParam(required = false) Long camionId,
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(required = false) String direccionOrden,
            @RequestParam(required = false, defaultValue = "0") Integer pagina,
            @RequestParam(required = false, defaultValue = "20") Integer tamanoPagina) {

        try {
            FiltrosContenedorDTO filtros = FiltrosContenedorDTO.builder()
                    .clienteId(clienteId)
                    .clienteNombre(clienteNombre)
                    .estadoSolicitud(estadoSolicitud != null ?
                            back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud.valueOf(estadoSolicitud) : null)
                    .estadoTramo(estadoTramo != null ?
                            back.tpi.ms_GestionDeOperaciones.domain.EstadoTramo.valueOf(estadoTramo) : null)
                    .pesoMinimo(pesoMinimo)
                    .pesoMaximo(pesoMaximo)
                    .volumenMinimo(volumenMinimo)
                    .volumenMaximo(volumenMaximo)
                    .fechaSolicitudDesde(fechaSolicitudDesde)
                    .fechaSolicitudHasta(fechaSolicitudHasta)
                    .ciudadOrigen(ciudadOrigen)
                    .ciudadDestino(ciudadDestino)
                    .costoMinimo(costoMinimo)
                    .costoMaximo(costoMaximo)
                    .soloAtrasados(soloAtrasados)
                    .camionId(camionId)
                    .ordenarPor(ordenarPor)
                    .direccionOrden(direccionOrden)
                    .pagina(pagina)
                    .tamanoPagina(tamanoPagina)
                    .build();

            ConsultaContenedoresResponseDTO response = service.consultarContenedoresPendientes(filtros);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Consulta con filtros en el body (más complejo)
     * POST /api/contenedores-pendientes/buscar
     */
    @PostMapping("/buscar")
    public ResponseEntity<ConsultaContenedoresResponseDTO> buscarConFiltros(
            @RequestBody FiltrosContenedorDTO filtros) {
        try {
            ConsultaContenedoresResponseDTO response = service.consultarContenedoresPendientes(filtros);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene la ubicación actual de un contenedor específico
     * GET /api/contenedores-pendientes/{contenedorId}/ubicacion
     */
    @GetMapping("/{contenedorId}/ubicacion")
    public ResponseEntity<ContenedorPendienteDTO> obtenerUbicacionContenedor(
            @PathVariable Long contenedorId) {
        try {
            ContenedorPendienteDTO contenedor = service.obtenerUbicacionContenedor(contenedorId);
            return ResponseEntity.ok(contenedor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene estadísticas rápidas de contenedores pendientes
     * GET /api/contenedores-pendientes/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasRapidas() {
        try {
            Map<String, Object> estadisticas = service.obtenerEstadisticasRapidas();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
