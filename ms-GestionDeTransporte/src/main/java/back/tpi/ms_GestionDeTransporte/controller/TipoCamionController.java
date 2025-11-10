package back.tpi.ms_GestionDeTransporte.controller;

import back.tpi.ms_GestionDeTransporte.dto.TipoCamionRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.TipoCamionResponseDTO;
import back.tpi.ms_GestionDeTransporte.service.TipoCamionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-camion")
@RequiredArgsConstructor
public class TipoCamionController {

    private final TipoCamionService tipoCamionService;

    @PostMapping
    public ResponseEntity<TipoCamionResponseDTO> crearTipoCamion(
            @Valid @RequestBody TipoCamionRequestDTO requestDTO) {
        TipoCamionResponseDTO response = tipoCamionService.crearTipoCamion(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoCamionResponseDTO> obtenerTipoCamion(@PathVariable Long id) {
        TipoCamionResponseDTO response = tipoCamionService.obtenerTipoCamionPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TipoCamionResponseDTO>> obtenerTodosTiposCamion() {
        List<TipoCamionResponseDTO> response = tipoCamionService.obtenerTodosTiposCamion();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoCamionResponseDTO> actualizarTipoCamion(
            @PathVariable Long id,
            @Valid @RequestBody TipoCamionRequestDTO requestDTO) {
        TipoCamionResponseDTO response = tipoCamionService.actualizarTipoCamion(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoCamion(@PathVariable Long id) {
        tipoCamionService.eliminarTipoCamion(id);
        return ResponseEntity.noContent().build();
    }
}