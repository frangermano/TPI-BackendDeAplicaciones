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
    public ResponseEntity<TarifaDTO> crearSolicitudCompleta(@RequestBody TarifaDTO tarifaDTO) {
        try {
            Tarifa tarifaCreada = service.crearTarifa(tarifaDTO);

            //  Convertir la entidad guardada de vuelta a DTO
            TarifaDTO respuesta = convertirADTO(tarifaCreada);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeTarifa(@PathVariable Long id) {
        return ResponseEntity.ok(service.existeTarifa(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaDTO> obtenerTarifa(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(tarifa -> {
                    TarifaDTO respuesta = convertirADTO(tarifa);
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    /*
    @GetMapping("/{id}/calcular-costo-total")
    public ResponseEntity<Double> calcularCosto(
            @PathVariable Long id) {
        try {
            Double costo = service.calcularCosto(id);
            return ResponseEntity.ok(costo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

     */


    @GetMapping("/{id}/calcular-costo-estimado")
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

    private TarifaDTO convertirADTO(Tarifa tarifa) {
        return TarifaDTO.builder()
                .id(tarifa.getId())
                .nombre(tarifa.getNombre())
                .patenteCamion(tarifa.getPatenteCamion())
                .valorCombustibleLitro(tarifa.getValorCombustibleLitro())
                .cargoGestionTrama(tarifa.getCargoGestionTrama())
                .fechaVigencia(tarifa.getFechaVigencia())
                .idTipoCamion(tarifa.getIdTipoCamion())
                .idDeposito(tarifa.getIdDeposito())
                .build();
    }

}
