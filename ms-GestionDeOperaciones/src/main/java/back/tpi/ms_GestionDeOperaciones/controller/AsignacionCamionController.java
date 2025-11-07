package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.AsignacionCamionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignacion-camiones")
@RequiredArgsConstructor
public class AsignacionCamionController {

    private final AsignacionCamionService service;

    /**
     * Asigna un camión a un tramo con validación de capacidad
     *
     * POST /api/asignacion-camiones/asignar
     */
    @PostMapping("/asignar")
    public ResponseEntity<?> asignarCamionATramo(@RequestBody AsignarCamionDTO asignacionDTO) {
        try {
            TramoDTO tramoActualizado = service.asignarCamionATramo(asignacionDTO);
            return ResponseEntity.ok(tramoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obtiene camiones disponibles que pueden transportar un contenedor
     *
     * GET /api/asignacion-camiones/disponibles/contenedor/{contenedorId}
     */
    @GetMapping("/disponibles/contenedor/{contenedorId}")
    public ResponseEntity<List<CamionDTO>> obtenerCamionesDisponiblesParaContenedor(
            @PathVariable Long contenedorId) {
        try {
            List<CamionDTO> camiones = service.obtenerCamionesDisponiblesParaContenedor(contenedorId);
            return ResponseEntity.ok(camiones);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene camiones disponibles para un tramo específico
     *
     * GET /api/asignacion-camiones/disponibles/tramo/{tramoId}
     */
    @GetMapping("/disponibles/tramo/{tramoId}")
    public ResponseEntity<List<CamionDTO>> obtenerCamionesDisponiblesParaTramo(
            @PathVariable Long tramoId) {
        try {
            List<CamionDTO> camiones = service.obtenerCamionesDisponiblesParaTramo(tramoId);
            return ResponseEntity.ok(camiones);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Libera un camión de un tramo
     *
     * DELETE /api/asignacion-camiones/tramo/{tramoId}/camion
     */
    @DeleteMapping("/tramo/{tramoId}/camion")
    public ResponseEntity<?> liberarCamionDeTramo(@PathVariable Long tramoId) {
        try {
            TramoDTO tramoActualizado = service.liberarCamionDeTramo(tramoId);
            return ResponseEntity.ok(tramoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // DTO para respuestas de error
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ErrorResponse {
        private String mensaje;
    }
}
