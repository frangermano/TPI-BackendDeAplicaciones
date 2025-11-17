package back.tpi.ms_GestionDeInfraestructura.controller;

import back.tpi.ms_GestionDeInfraestructura.dto.DepositoDTO;
import back.tpi.ms_GestionDeInfraestructura.service.DepositoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Deposito", description = "Operaciones relacionadas con depósitos")
@RestController
@RequestMapping("/api/depositos")
@RequiredArgsConstructor
@Slf4j
public class DepositoController {

    private final DepositoService depositoService;

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Obtener todos los depósitos",
            description = "Retorna una lista completa con todos los depósitos registrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Listado obtenido correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DepositoDTO.class))
    )
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

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Obtener depósito por ID",
            description = "Retorna la información de un depósito especificado por ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito encontrado"),
            @ApiResponse(responseCode = "404", description = "Depósito no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DepositoDTO> obtenerDepositoPorId(
            @Parameter(description = "ID del depósito a consultar")
            @PathVariable Long id) {
        try {
            DepositoDTO deposito = depositoService.obtenerPorId(id);
            return ResponseEntity.ok(deposito);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Verificar si existe un depósito por ID",
            description = "Devuelve true si existe un depósito con ese ID."
    )
    @ApiResponse(responseCode = "200", description = "Existencia verificada")
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeDeposito(
            @Parameter(description = "ID del depósito a verificar")
            @PathVariable Long id) {
        boolean existe = depositoService.existe(id);
        return ResponseEntity.ok(existe);
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Obtener depósitos en ruta",
            description = "Retorna los depósitos más cercanos a la ruta entre origen y destino."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósitos encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/en-ruta")
    public ResponseEntity<List<DepositoDTO>> obtenerDepositosEnRuta(
            @Parameter(description = "Latitud de origen") @RequestParam Double latOrigen,
            @Parameter(description = "Longitud de origen") @RequestParam Double lngOrigen,
            @Parameter(description = "Latitud de destino") @RequestParam Double latDestino,
            @Parameter(description = "Longitud de destino") @RequestParam Double lngDestino,
            @Parameter(description = "Cantidad de depósitos a retornar (default 3)")
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

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Obtener depósitos cercanos a un punto",
            description = "Retorna depósitos dentro de un radio definido desde una ubicación geográfica."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósitos encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/cercanos")
    public ResponseEntity<List<DepositoDTO>> obtenerDepositosCercanos(
            @Parameter(description = "Latitud del punto de referencia") @RequestParam Double lat,
            @Parameter(description = "Longitud del punto de referencia") @RequestParam Double lng,
            @Parameter(description = "Radio en kilómetros a buscar (default 50)")
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

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Crear un nuevo depósito",
            description = "Crea un depósito nuevo y devuelve la entidad creada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos enviados")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<DepositoDTO> crearDeposito(
            @Parameter(description = "Datos del nuevo depósito")
            @RequestBody DepositoDTO depositoDTO) {
        try {
            DepositoDTO depositoCreado = depositoService.crear(depositoDTO);
            return ResponseEntity.ok(depositoCreado);
        } catch (Exception e) {
            log.error("Error al crear depósito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Actualizar un depósito existente",
            description = "Actualiza los datos de un depósito especificado por ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Depósito no encontrado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos enviados")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<DepositoDTO> actualizarDeposito(
            @Parameter(description = "ID del depósito a actualizar")
            @PathVariable Long id,
            @Parameter(description = "Nuevos datos del depósito")
            @RequestBody DepositoDTO depositoDTO) {
        try {
            DepositoDTO depositoActualizado = depositoService.actualizar(id, depositoDTO);
            return ResponseEntity.ok(depositoActualizado);
        } catch (RuntimeException e) {
            log.error("Error al actualizar depósito: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al actualizar depósito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Eliminar un depósito",
            description = "Elimina un depósito del sistema especificado por ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Depósito eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Depósito no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarDeposito(
            @Parameter(description = "ID del depósito a eliminar")
            @PathVariable Long id) {
        try {
            depositoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error al eliminar depósito: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al eliminar depósito: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}