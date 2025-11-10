package back.tpi.ms_GestionDeTransporte.controller;

import back.tpi.ms_GestionDeTransporte.dto.TransportistaRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.TransportistaResponseDTO;
import back.tpi.ms_GestionDeTransporte.service.TransportistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
@RequiredArgsConstructor
public class TransportistaController {

    private final TransportistaService transportistaService;

    @PostMapping
    public ResponseEntity<TransportistaResponseDTO> registrarTransportista(
            @Valid @RequestBody TransportistaRequestDTO requestDTO) {
        TransportistaResponseDTO response = transportistaService.registrarTransportista(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportistaResponseDTO> obtenerTransportista(@PathVariable Long id) {
        TransportistaResponseDTO response = transportistaService.obtenerTransportistaPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransportistaResponseDTO>> obtenerTodosLosTransportistas() {
        List<TransportistaResponseDTO> response = transportistaService.obtenerTodosLosTransportistas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<TransportistaResponseDTO>> obtenerTransportistasDisponibles() {
        List<TransportistaResponseDTO> response = transportistaService.obtenerTransportistasDisponibles();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransportistaResponseDTO> actualizarTransportista(
            @PathVariable Long id,
            @Valid @RequestBody TransportistaRequestDTO requestDTO) {
        TransportistaResponseDTO response = transportistaService.actualizarTransportista(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransportista(@PathVariable Long id) {
        transportistaService.eliminarTransportista(id);
        return ResponseEntity.noContent().build();
    }
}
