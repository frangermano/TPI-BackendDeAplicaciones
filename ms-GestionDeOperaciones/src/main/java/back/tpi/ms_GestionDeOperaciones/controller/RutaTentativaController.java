package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.domain.Ruta;
import back.tpi.ms_GestionDeOperaciones.domain.SolicitudTraslado;
import back.tpi.ms_GestionDeOperaciones.dto.*;
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

    @GetMapping("/solicitud/{solicitudId}")
    public ResponseEntity<List<RutaTentativaDTO>> consultarRutasTentativas(
            @PathVariable Long solicitudId) {
        try {
            List<RutaTentativaDTO> rutas = rutaTentativaService
                    .consultarRutasTentativas(solicitudId);
            return ResponseEntity.ok(rutas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/confirmar")
    public ResponseEntity<RutaDTO> confirmarRutaTentativa(
            @RequestBody ConfirmarRutaTentativaDTO confirmarDTO) {
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