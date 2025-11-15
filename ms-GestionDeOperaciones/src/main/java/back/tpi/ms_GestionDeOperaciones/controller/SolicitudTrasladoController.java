package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.client.TarifaClient;
import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import back.tpi.ms_GestionDeOperaciones.domain.SolicitudTraslado;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.service.CalculoCostoService;
import back.tpi.ms_GestionDeOperaciones.service.SolicitudTrasladoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Solicitudes de Traslado", description = "Operaciones relacionadas con solicitudes de traslado")
@RestController
@RequestMapping("/api/solicitudes-traslado")
@RequiredArgsConstructor
public class SolicitudTrasladoController {

    private final SolicitudTrasladoService service;
    private final CalculoCostoService calculoCostoService;
    private final TarifaClient tarifaClient;
    private final ClienteController clienteController;
    private final ContenedorController contenedorController;
    private final RutaController rutaController;

    // -------------------------------------------------------------------------
    // REQUERIMIENTO 1 - Crear solicitud completa
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Crear una solicitud completa",
            description = "Registra/verifica cliente, crea contenedor y genera una solicitud en estado PENDIENTE."
    )
    @ApiResponse(responseCode = "201", description = "Solicitud creada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @PostMapping("/completa")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<SolicitudTrasladoDTO> crearSolicitudCompleta(@RequestBody SolicitudTrasladoDTO solicitudDTO) {
        try {
            SolicitudTrasladoDTO nuevaSolicitud = service.crearSolicitudCompleta(solicitudDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSolicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // -------------------------------------------------------------------------
    // REQUERIMIENTO 2 - Estado por solicitud
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Consultar estado por ID de solicitud",
            description = "Devuelve el estado actual del transporte asociado a una solicitud."
    )
    @ApiResponse(responseCode = "200", description = "Estado obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    @GetMapping("/estado/solicitud/{solicitudId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<EstadoTransporteDTO> consultarEstadoPorSolicitud(@PathVariable Long solicitudId) {
        try {
            EstadoTransporteDTO estado = service.consultarEstadoPorSolicitud(solicitudId);
            return ResponseEntity.ok(estado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------------------------------------------------------------
    // REQUERIMIENTO 2 - Estado por contenedor
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Consultar estado por ID de contenedor",
            description = "Devuelve el estado del transporte buscándolo por el contenedor."
    )
    @ApiResponse(responseCode = "200", description = "Estado obtenido")
    @ApiResponse(responseCode = "404", description = "Contenedor no encontrado")
    @GetMapping("/estado/contenedor/{contenedorId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<EstadoTransporteDTO> consultarEstadoPorContenedor(@PathVariable Long contenedorId) {
        try {
            EstadoTransporteDTO estado = service.consultarEstadoPorContenedor(contenedorId);
            return ResponseEntity.ok(estado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------------------------------------------------------------
    // Calcular costo real (ADMIN)
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Calcular costo real",
            description = "Calcula el costo real total de la solicitud de traslado."
    )
    @ApiResponse(responseCode = "200", description = "Costo calculado correctamente")
    @PostMapping("/{id}/calcular-costo-real")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CostoDetalleDTO> calcularCostoReal(@PathVariable Long id) {
        return ResponseEntity.ok(service.calcularCostoReal(id));
    }

    // -------------------------------------------------------------------------
    // Listar todas (ADMIN)
    // -------------------------------------------------------------------------
    @Operation(summary = "Obtener todas las solicitudes")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<SolicitudTrasladoDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // -------------------------------------------------------------------------
    // Obtener por ID (Admin + Cliente)
    // -------------------------------------------------------------------------
    @Operation(summary = "Obtener solicitud por ID")
    @ApiResponse(responseCode = "200", description = "Solicitud encontrada")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CLIENTE')")
    public ResponseEntity<SolicitudTrasladoDTO> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------------------------------------------------------------
    // Obtener por Estado
    // -------------------------------------------------------------------------
    @Operation(summary = "Obtener solicitudes por estado")
    @ApiResponse(responseCode = "200", description = "Listado encontrado")
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CLIENTE')")
    public ResponseEntity<List<SolicitudTraslado>> obtenerPorEstado(@PathVariable EstadoSolicitud estado) {
        return ResponseEntity.ok(service.obtenerPorEstado(estado));
    }

    // -------------------------------------------------------------------------
    // Actualizar estado (ADMIN)
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Actualizar estado",
            description = "Actualiza el estado de una solicitud de traslado."
    )
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SolicitudTrasladoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoSolicitud nuevoEstado) {
        try {
            return ResponseEntity.ok(service.actualizarEstado(id, nuevoEstado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // -------------------------------------------------------------------------
    // Finalizar solicitud (ADMIN)
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Finalizar solicitud",
            description = "Marca la solicitud como finalizada y registra el costo final."
    )
    @ApiResponse(responseCode = "200", description = "Solicitud finalizada correctamente")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SolicitudTraslado> finalizarSolicitud(
            @PathVariable Long id,
            @RequestParam Double costoFinal,
            @RequestParam String tiempoReal) {
        try {
            return ResponseEntity.ok(service.finalizarSolicitud(id, costoFinal, tiempoReal));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------------------------------------------------------------
    // Eliminar solicitud (ADMIN)
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Eliminar solicitud",
            description = "Elimina una solicitud por ID."
    )
    @ApiResponse(responseCode = "204", description = "Solicitud eliminada")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Long id) {
        try {
            service.eliminarSolicitud(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
