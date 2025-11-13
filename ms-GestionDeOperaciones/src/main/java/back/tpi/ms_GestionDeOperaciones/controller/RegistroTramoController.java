package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.RegistroTramoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registro-tramos")
@RequiredArgsConstructor
public class RegistroTramoController {

    private final RegistroTramoService service;

    /**
     * El transportista registra el INICIO de un tramo
     *
     * POST /api/registro-tramos/iniciar
     */
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarTramo(@RequestBody RegistroTramoDTO registroDTO) {
        try {
            RegistroTramoResponseDTO response = service.registrarInicioTramo(registroDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * El transportista registra el FIN de un tramo
     *
     * POST /api/registro-tramos/finalizar
     */
    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarTramo(@RequestBody RegistroTramoDTO registroDTO) {
        try {
            RegistroTramoResponseDTO response = service.registrarFinTramo(registroDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obtiene el estado actual de un tramo
     *
     * GET /api/registro-tramos/{tramoId}
     */
    @GetMapping("/{tramoId}")
    public ResponseEntity<TramoDTO> obtenerEstadoTramo(@PathVariable Long tramoId) {
        try {
            TramoDTO tramo = service.obtenerEstadoTramo(tramoId);
            return ResponseEntity.ok(tramo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // DTO para respuestas de error
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ErrorResponse {
        private String mensaje;
    }
}
