package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.domain.Ruta;
import back.tpi.ms_GestionDeOperaciones.domain.SolicitudTraslado;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.RutaService;
import back.tpi.ms_GestionDeOperaciones.service.RutaTentativaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas-tentativas")
@RequiredArgsConstructor
public class RutaTentativaController {

    private final RutaTentativaService rutaTentativaService;
    private final RutaService rutaService;

    /**
     * Consulta rutas tentativas para una solicitud
     * GET /api/rutas-tentativas/solicitud/{solicitudId}
     *
     * Retorna múltiples opciones de rutas con:
     * - Tramos sugeridos
     * - Costos estimados
     * - Tiempos estimados
     * - Ventajas y desventajas
     */
    @GetMapping("/solicitud/{solicitudId}")
    public ResponseEntity<ConsultaRutasResponseDTO> consultarRutasTentativas(
            @RequestBody SolicitudTraslado solicitudId) {
        try {
            List<Ruta> response = rutaTentativaService.consultarRutasTentativas(solicitudId);
            return ResponseEntity.ok((ConsultaRutasResponseDTO) response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Confirma y asigna una ruta tentativa seleccionada
     * POST /api/rutas-tentativas/confirmar
     *
     * Convierte la ruta tentativa elegida en una ruta real asignada
     */
    @PostMapping("/confirmar")
    public ResponseEntity<RutaDTO> confirmarRutaTentativa(
            @RequestBody ConfirmarRutaTentativaDTO confirmarDTO) {
        try {
            // Convertir ruta tentativa a AsignarRutaDTO
            SolicitudTraslado asignarRutaDTO = rutaTentativaService.asignarRutaASolicitud(
                    confirmarDTO.getSolicitudTrasladoId(),
                    Long.valueOf(confirmarDTO.getNumeroOpcionSeleccionada())
            );

            // Asignar la ruta real
            RutaDTO rutaAsignada = rutaService.asignarRutaConTramos(asignarRutaDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(rutaAsignada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
