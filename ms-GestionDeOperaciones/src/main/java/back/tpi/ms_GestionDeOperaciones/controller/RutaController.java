package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoTramo;
import back.tpi.ms_GestionDeOperaciones.domain.Ruta;
import back.tpi.ms_GestionDeOperaciones.domain.Tramo;
import back.tpi.ms_GestionDeOperaciones.dto.AsignarRutaDTO;
import back.tpi.ms_GestionDeOperaciones.dto.RutaDTO;
import back.tpi.ms_GestionDeOperaciones.dto.TramoDTO;
import back.tpi.ms_GestionDeOperaciones.service.RutaService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    /**
     * Asigna una ruta completa con todos sus tramos a una solicitud
     * POST /api/rutas/asignar
     */
    @PostMapping("/asignar")
    public ResponseEntity<RutaDTO> asignarRutaConTramos(@RequestBody AsignarRutaDTO asignarRutaDTO) {
        try {
            RutaDTO rutaCreada = rutaService.asignarRutaConTramos(asignarRutaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(rutaCreada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene la ruta asignada a una solicitud
     * GET /api/rutas/solicitud/{solicitudId}
     */
    @GetMapping("/solicitud/{solicitudId}")
    public ResponseEntity<RutaDTO> obtenerRutaPorSolicitud(@PathVariable Long solicitudId) {
        try {
            RutaDTO ruta = rutaService.obtenerRutaPorSolicitud(solicitudId);
            return ResponseEntity.ok(ruta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene todos los tramos de una ruta
     * GET /api/rutas/{rutaId}/tramos
     */
    @GetMapping("/{rutaId}/tramos")
    public ResponseEntity<List<TramoDTO>> obtenerTramosPorRuta(@PathVariable Long rutaId) {
        try {
            List<TramoDTO> tramos = rutaService.obtenerTramosPorRuta(rutaId);
            return ResponseEntity.ok(tramos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Elimina una ruta y todos sus tramos
     * DELETE /api/rutas/{rutaId}
     */
    @DeleteMapping("/{rutaId}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long rutaId) {
        try {
            rutaService.eliminarRuta(rutaId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}