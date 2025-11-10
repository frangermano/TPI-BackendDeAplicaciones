package back.tpi.ms_GestionDeTransporte.controller;

import back.tpi.ms_GestionDeTransporte.dto.CamionRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.CamionResponseDTO;
import back.tpi.ms_GestionDeTransporte.service.CamionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/camiones")
@RequiredArgsConstructor
public class CamionController {

    private final CamionService camionService;

    @PostMapping
    public ResponseEntity<CamionResponseDTO> registrarCamion(
            @Valid @RequestBody CamionRequestDTO requestDTO) {
        CamionResponseDTO response = camionService.registrarCamion(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{patente}")
    public ResponseEntity<CamionResponseDTO> obtenerCamion(@PathVariable String patente) {
        CamionResponseDTO response = camionService.obtenerCamionPorPatente(patente);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CamionResponseDTO>> obtenerTodosLosCamiones() {
        List<CamionResponseDTO> response = camionService.obtenerTodosLosCamiones();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<CamionResponseDTO>> obtenerCamionesDisponibles() {
        List<CamionResponseDTO> response = camionService.obtenerCamionesDisponibles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transportista/{transportistaId}")
    public ResponseEntity<List<CamionResponseDTO>> obtenerCamionesPorTransportista(
            @PathVariable Long transportistaId) {
        List<CamionResponseDTO> response = camionService.obtenerCamionesPorTransportista(transportistaId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{patente}")
    public ResponseEntity<CamionResponseDTO> actualizarCamion(
            @PathVariable String patente,
            @Valid @RequestBody CamionRequestDTO requestDTO) {
        CamionResponseDTO response = camionService.actualizarCamion(patente, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{patente}")
    public ResponseEntity<Void> eliminarCamion(@PathVariable String patente) {
        camionService.eliminarCamion(patente);
        return ResponseEntity.noContent().build();
    }
}