package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.ContenedorPendienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenedores-pendientes")
@RequiredArgsConstructor
public class ContenedorPendienteController {

    private final ContenedorPendienteService service;

    /**
     * Consulta todos los contenedores pendientes con filtros opcionales
     *
     * GET /api/contenedores-pendientes
     * GET /api/contenedores-pendientes?estado=EN_PROCESO
     * GET /api/contenedores-pendientes?clienteId=5
     * GET /api/contenedores-pendientes?soloAtrasados=true
     * GET /api/contenedores-pendientes?estado=EN_PROCESO&clienteId=5
     */
    @GetMapping
    public ResponseEntity<List<ContenedorPendienteDTO>> consultarContenedoresPendientes(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Boolean soloAtrasados) {

        try {
            FiltrosContenedorDTO filtros = FiltrosContenedorDTO.builder()
                    .estado(estado != null ? EstadoSolicitud.valueOf(estado) : null)
                    .clienteId(clienteId)
                    .soloAtrasados(soloAtrasados)
                    .build();

            List<ContenedorPendienteDTO> contenedores =
                    service.consultarContenedoresPendientes(filtros);

            return ResponseEntity.ok(contenedores);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene la ubicación actual de un contenedor específico
     *
     * GET /api/contenedores-pendientes/{contenedorId}/ubicacion
     */
    @GetMapping("/{contenedorId}/ubicacion")
    public ResponseEntity<ContenedorPendienteDTO> obtenerUbicacionContenedor(
            @PathVariable Long contenedorId) {
        try {
            ContenedorPendienteDTO contenedor =
                    service.obtenerUbicacionContenedor(contenedorId);
            return ResponseEntity.ok(contenedor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}