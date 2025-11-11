package back.tpi.ms_GestionDeInfraestructura.controller;

import back.tpi.ms_GestionDeInfraestructura.dto.DepositoDTO;
import back.tpi.ms_GestionDeInfraestructura.service.DepositoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depositos")
@RequiredArgsConstructor
@Slf4j
public class DepositoController {

    private final DepositoService depositoService;

    /**
     * Obtiene todos los depósitos
     */
    @GetMapping
    public ResponseEntity<List<DepositoDTO>> obtenerTodosLosDepositos() {
        try {
            List<DepositoDTO> depositos = depositoService.obtenerTodos();
            return ResponseEntity.ok(depositos);
        } catch (Exception e) {
            log.error("Error al obtener depósitos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene un depósito por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepositoDTO> obtenerDepositoPorId(@PathVariable Long id) {
        try {
            DepositoDTO deposito = depositoService.obtenerPorId(id);
            return ResponseEntity.ok(deposito);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verifica si existe un depósito
     */
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeDeposito(@PathVariable Long id) {
        boolean existe = depositoService.existe(id);
        return ResponseEntity.ok(existe);
    }

    /**
     * Obtiene depósitos que están cerca de la ruta entre origen y destino
     */
    @GetMapping("/en-ruta")
    public ResponseEntity<List<DepositoDTO>> obtenerDepositosEnRuta(
            @RequestParam Double latOrigen,
            @RequestParam Double lngOrigen,
            @RequestParam Double latDestino,
            @RequestParam Double lngDestino,
            @RequestParam(defaultValue = "3") Integer cantidad) {

        try {
            log.info("Buscando depósitos en ruta: origen({},{}) destino({},{})",
                    latOrigen, lngOrigen, latDestino, lngDestino);

            List<DepositoDTO> depositos = depositoService.encontrarDepositosEnRuta(
                    latOrigen, lngOrigen, latDestino, lngDestino, cantidad);

            return ResponseEntity.ok(depositos);
        } catch (Exception e) {
            log.error("Error al buscar depósitos en ruta: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene depósitos cercanos a un punto
     */
    @GetMapping("/cercanos")
    public ResponseEntity<List<DepositoDTO>> obtenerDepositosCercanos(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "50") Double radioKm) {

        try {
            List<DepositoDTO> depositos = depositoService.encontrarDepositosCercanos(
                    lat, lng, radioKm);
            return ResponseEntity.ok(depositos);
        } catch (Exception e) {
            log.error("Error al buscar depósitos cercanos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crea un nuevo depósito
     */
    @PostMapping
    public ResponseEntity<DepositoDTO> crearDeposito(@RequestBody DepositoDTO depositoDTO) {
        try {
            DepositoDTO depositoCreado = depositoService.crear(depositoDTO);
            return ResponseEntity.ok(depositoCreado);
        } catch (Exception e) {
            log.error("Error al crear depósito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}