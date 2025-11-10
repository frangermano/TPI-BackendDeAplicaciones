package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import back.tpi.ms_GestionDeOperaciones.domain.SolicitudTraslado;
import back.tpi.ms_GestionDeOperaciones.dto.CostoDetalleDTO;
import back.tpi.ms_GestionDeOperaciones.dto.EstadoTransporteDTO;
import back.tpi.ms_GestionDeOperaciones.dto.SolicitudTrasladoDTO;
import back.tpi.ms_GestionDeOperaciones.service.CalculoCostoService;
import back.tpi.ms_GestionDeOperaciones.service.SolicitudTrasladoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-traslado")
@RequiredArgsConstructor
public class SolicitudTrasladoController {

    private final SolicitudTrasladoService service;
    private final CalculoCostoService calculoCostoService;

    /**
     * Crea una solicitud COMPLETA:
     * - Registra/verifica cliente si no existe
     * - Crea contenedor con identificación única
     * - Registra solicitud con estado PENDIENTE
     */
    // REQUERIMIENTO 1
    @PostMapping("/completa")
    public ResponseEntity<SolicitudTraslado> crearSolicitudCompleta(@RequestBody SolicitudTrasladoDTO solicitudDTO) {
        try {
            SolicitudTraslado nuevaSolicitud = service.crearSolicitudCompleta(solicitudDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSolicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // REQUERIMIENTO 2
    /**
     * Consultar el estado del transporte por ID de solicitud
     * GET /api/solicitudes-traslado/estado/{solicitudId}
     */

    @GetMapping("/estado/solicitud/{solicitudId}")
    public ResponseEntity<EstadoTransporteDTO> consultarEstadoPorSolicitud(@PathVariable Long solicitudId) {
        try {
            EstadoTransporteDTO estado = service.consultarEstadoPorSolicitud(solicitudId);
            return ResponseEntity.ok(estado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Consultar el estado del transporte por ID de contenedor
     * GET /api/solicitudes-traslado/estado/contenedor/{contenedorId}
     */
    @GetMapping("/estado/contenedor/{contenedorId}")
    public ResponseEntity<EstadoTransporteDTO> consultarEstadoPorContenedor(@PathVariable Long contenedorId) {
        try {
            EstadoTransporteDTO estado = service.consultarEstadoPorContenedor(contenedorId);
            return ResponseEntity.ok(estado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Calcula el costo total de una solicitud de traslado
     *
     * @param id ID de la solicitud de traslado
     * @return Detalle completo del cálculo de costos
     */
    @PostMapping("/{id}/calcular-costo")
    public ResponseEntity<CostoDetalleDTO> calcularCosto(@PathVariable Long id) {
        CostoDetalleDTO costoDetalle = calculoCostoService.calcularYActualizarCosto(id);
        return ResponseEntity.ok(costoDetalle);
    }

    /**
     * Obtiene el detalle del costo sin recalcular (si ya fue calculado)
     *
     * @param id ID de la solicitud de traslado
     * @return Detalle del costo
     */
    @GetMapping("/{id}/detalle-costo")
    public ResponseEntity<CostoDetalleDTO> obtenerDetalleCosto(@PathVariable Long id) {
        CostoDetalleDTO costoDetalle = calculoCostoService.calcularYActualizarCosto(id);
        return ResponseEntity.ok(costoDetalle);
    }

    @GetMapping
    public ResponseEntity<List<SolicitudTraslado>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudTraslado> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudTraslado>> obtenerPorEstado(@PathVariable EstadoSolicitud estado) {
        return ResponseEntity.ok(service.obtenerPorEstado(estado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudTraslado> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoSolicitud nuevoEstado) {
        try {
            return ResponseEntity.ok(service.actualizarEstado(id, nuevoEstado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<SolicitudTraslado> finalizarSolicitud(
            @PathVariable Long id,
            @RequestParam Double costoFinal,
            @RequestParam Double tiempoReal) {
        try {
            return ResponseEntity.ok(service.finalizarSolicitud(id, costoFinal, tiempoReal));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Long id) {
        try {
            service.eliminarSolicitud(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}