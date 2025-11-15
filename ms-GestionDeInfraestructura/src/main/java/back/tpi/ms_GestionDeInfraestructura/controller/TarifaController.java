package back.tpi.ms_GestionDeInfraestructura.controller;

import back.tpi.ms_GestionDeInfraestructura.domain.Tarifa;
import back.tpi.ms_GestionDeInfraestructura.dto.TarifaDTO;
import back.tpi.ms_GestionDeInfraestructura.mapper.TarifaMapper;
import back.tpi.ms_GestionDeInfraestructura.service.TarifaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tarifa", description = "Operaciones relacionadas con tarifas")
@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
@Slf4j
public class TarifaController {

    private final TarifaService service;
    private final TarifaMapper tarifaMapper;

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Crear una nueva tarifa",
            description = "Permite crear una tarifa. Retorna la tarifa creada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarifa creada correctamente",
                    content = @Content(schema = @Schema(implementation = TarifaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o error al crear tarifa")
    })
    @PostMapping
    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TarifaDTO> crearSolicitudCompleta(
            @Parameter(description = "Datos de la tarifa a crear")
            @RequestBody TarifaDTO tarifaDTO) {
        try {
            Tarifa tarifaCreada = service.crearTarifa(tarifaDTO);
            TarifaDTO respuesta = tarifaMapper.toDTO(tarifaCreada);
            // LOG para debugging
            log.info("✅ Tarifa creada y mapeada correctamente: ID={}, Nombre={}",
                    respuesta.getTarifaId(), respuesta.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Verificar si existe una tarifa",
            description = "Devuelve true si existe una tarifa con el ID indicado."
    )
    @ApiResponse(responseCode = "200", description = "Existencia verificada")
    @GetMapping("/{id}/existe")
    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Boolean> existeTarifa(
            @Parameter(description = "ID de la tarifa a verificar")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.existeTarifa(id));
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Obtener una tarifa por ID",
            description = "Retorna una tarifa si existe, caso contrario devuelve 404."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarifa encontrada",
                    content = @Content(schema = @Schema(implementation = TarifaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tarifa no encontrada")
    })
    @GetMapping("/{id}")
    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TarifaDTO> obtenerTarifa(
            @Parameter(description = "ID de la tarifa a obtener")
            @PathVariable Long id) {

        return service.obtenerPorId(id)
                .map(tarifa -> ResponseEntity.ok(tarifaMapper.toDTO(tarifa)))
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Calcular el costo estimado",
            description = "Calcula el costo estimado de un traslado según la tarifa indicada y la distancia."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Costo calculado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en parámetros o tarifa inexistente")
    })
    @GetMapping("/{id}/calcular-costo-estimado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<Double> calcularCostoEstimado(
            @Parameter(description = "ID de la tarifa utilizada para el cálculo")
            @PathVariable Long id,
            @Parameter(description = "Distancia en km para calcular el costo estimado")
            @RequestParam Double distancia) {
        try {
            Double costo = service.calcularCostoEstimado(id, distancia);
            return ResponseEntity.ok(costo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
