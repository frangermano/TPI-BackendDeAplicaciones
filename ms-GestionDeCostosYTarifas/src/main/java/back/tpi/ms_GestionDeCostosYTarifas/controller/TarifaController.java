package back.tpi.ms_GestionDeCostosYTarifas.controller;

import back.tpi.ms_GestionDeCostosYTarifas.domain.Tarifa;
import back.tpi.ms_GestionDeCostosYTarifas.service.TarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final TarifaService service;

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

