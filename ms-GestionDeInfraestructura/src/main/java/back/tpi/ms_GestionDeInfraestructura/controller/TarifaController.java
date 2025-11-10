package back.tpi.ms_GestionDeInfraestructura.controller;

import back.tpi.ms_GestionDeInfraestructura.domain.Tarifa;
import back.tpi.ms_GestionDeInfraestructura.dto.TarifaDTO;
import back.tpi.ms_GestionDeInfraestructura.service.TarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final TarifaService service;

    @PostMapping
    public ResponseEntity<Tarifa> crearSolicitudCompleta(@RequestBody TarifaDTO tarifaDTO) {
        try {
            Tarifa nuevaTarifa = service.crearTarifa(tarifaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTarifa);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeTarifa(@PathVariable Long id) {
        return ResponseEntity.ok(service.existeTarifa(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> obtenerTarifa(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/calcular-costo")
    public ResponseEntity<Double> calcularCosto(
            @PathVariable Long id,
            @RequestParam Double distancia) {
        try {
            Double costo = service.calcularCosto(id, distancia);
            return ResponseEntity.ok(costo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /*
    @GetMapping("/{id}/calcular-costo")
    public ResponseEntity<Double> calcularCostoEstimado(
            @PathVariable Long id,
            @RequestParam Double distancia) {
        try {
            Double costo = service.calcularCostoEstimado(id, distancia);
            return ResponseEntity.ok(costo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

     */
}
