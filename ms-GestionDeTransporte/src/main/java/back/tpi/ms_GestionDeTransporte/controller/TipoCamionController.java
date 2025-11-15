package back.tpi.ms_GestionDeTransporte.controller;

import back.tpi.ms_GestionDeTransporte.dto.TipoCamionRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.TipoCamionResponseDTO;
import back.tpi.ms_GestionDeTransporte.service.TipoCamionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Tipo de Camión",
        description = "Operaciones relacionadas con las distintas categorías o tipologías de camiones"
)
@RestController
@RequestMapping("/api/tipos-camion")
@RequiredArgsConstructor
public class TipoCamionController {

    private final TipoCamionService tipoCamionService;

    // ====================================================================================
    // CREAR
    // ====================================================================================
    @Operation(
            summary = "Crear un nuevo tipo de camión",
            description = "Permite registrar un nuevo tipo de camión. Solo accesible para ADMINISTRADOR."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de camión creado correctamente",
                    content = @Content(schema = @Schema(implementation = TipoCamionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TipoCamionResponseDTO> crearTipoCamion(
            @Valid @RequestBody TipoCamionRequestDTO requestDTO) {

        TipoCamionResponseDTO response = tipoCamionService.crearTipoCamion(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ====================================================================================
    // OBTENER POR ID
    // ====================================================================================
    @Operation(
            summary = "Obtener un tipo de camión por ID",
            description = "Devuelve un tipo de camión según su identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de camión encontrado",
                    content = @Content(schema = @Schema(implementation = TipoCamionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el tipo de camión"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<TipoCamionResponseDTO> obtenerTipoCamion(
            @Parameter(description = "ID del tipo de camión") @PathVariable Long id) {

        TipoCamionResponseDTO response = tipoCamionService.obtenerTipoCamionPorId(id);
        return ResponseEntity.ok(response);
    }

    // ====================================================================================
    // LISTAR TODOS
    // ====================================================================================
    @Operation(
            summary = "Obtener todos los tipos de camión",
            description = "Lista completa de categorías de camiones."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = TipoCamionResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<List<TipoCamionResponseDTO>> obtenerTodosTiposCamion() {

        List<TipoCamionResponseDTO> response = tipoCamionService.obtenerTodosTiposCamion();
        return ResponseEntity.ok(response);
    }

    // ====================================================================================
    // ACTUALIZAR
    // ====================================================================================
    @Operation(
            summary = "Actualizar un tipo de camión",
            description = "Modifica los atributos de un tipo de camión existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de camión actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = TipoCamionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el tipo de camión"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TipoCamionResponseDTO> actualizarTipoCamion(
            @Parameter(description = "ID del tipo de camión") @PathVariable Long id,
            @Valid @RequestBody TipoCamionRequestDTO requestDTO) {

        TipoCamionResponseDTO response = tipoCamionService.actualizarTipoCamion(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    // ====================================================================================
    // ELIMINAR
    // ====================================================================================
    @Operation(
            summary = "Eliminar un tipo de camión por ID",
            description = "Elimina permanentemente un tipo de camión. Solo ADMINISTRADOR."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el tipo de camión"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarTipoCamion(
            @Parameter(description = "ID del tipo de camión") @PathVariable Long id) {

        tipoCamionService.eliminarTipoCamion(id);
        return ResponseEntity.noContent().build();
    }
}
