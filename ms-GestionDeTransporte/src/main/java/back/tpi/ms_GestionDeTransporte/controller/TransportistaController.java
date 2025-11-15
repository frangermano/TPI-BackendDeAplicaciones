package back.tpi.ms_GestionDeTransporte.controller;

import back.tpi.ms_GestionDeTransporte.dto.TransportistaRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.TransportistaResponseDTO;
import back.tpi.ms_GestionDeTransporte.service.TransportistaService;
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
        name = "Transportista",
        description = "Operaciones relacionadas con la gestión de transportistas"
)
@RestController
@RequestMapping("/api/transportistas")
@RequiredArgsConstructor
public class TransportistaController {

    private final TransportistaService transportistaService;

    // ====================================================================================
    // CREAR TRANSPORTISTA
    // ====================================================================================
    @Operation(
            summary = "Registrar un nuevo transportista",
            description = "Permite crear un nuevo transportista. Solo el rol ADMINISTRADOR puede realizar esta operación."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transportista creado correctamente",
                    content = @Content(schema = @Schema(implementation = TransportistaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TransportistaResponseDTO> registrarTransportista(
            @Valid @RequestBody TransportistaRequestDTO requestDTO) {

        TransportistaResponseDTO response = transportistaService.registrarTransportista(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ====================================================================================
    // OBTENER POR ID
    // ====================================================================================
    @Operation(
            summary = "Obtener un transportista por ID",
            description = "Retorna la información de un transportista específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transportista encontrado",
                    content = @Content(schema = @Schema(implementation = TransportistaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transportista no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<TransportistaResponseDTO> obtenerTransportista(
            @Parameter(description = "ID del transportista") @PathVariable Long id) {

        TransportistaResponseDTO response = transportistaService.obtenerTransportistaPorId(id);
        return ResponseEntity.ok(response);
    }

    // ====================================================================================
    // LISTAR TODOS LOS TRANSPORTISTAS
    // ====================================================================================
    @Operation(
            summary = "Obtener todos los transportistas",
            description = "Devuelve una lista completa de transportistas registrados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = TransportistaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<List<TransportistaResponseDTO>> obtenerTodosLosTransportistas() {

        List<TransportistaResponseDTO> response = transportistaService.obtenerTodosLosTransportistas();
        return ResponseEntity.ok(response);
    }

    // ====================================================================================
    // LISTAR TRANSPORTISTAS DISPONIBLES
    // ====================================================================================
    @Operation(
            summary = "Obtener transportistas disponibles",
            description = "Lista los transportistas que actualmente están disponibles para asignación."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = TransportistaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<List<TransportistaResponseDTO>> obtenerTransportistasDisponibles() {

        List<TransportistaResponseDTO> response = transportistaService.obtenerTransportistasDisponibles();
        return ResponseEntity.ok(response);
    }

    // ====================================================================================
    // ACTUALIZAR TRANSPORTISTA
    // ====================================================================================
    @Operation(
            summary = "Actualizar un transportista",
            description = "Modifica los datos de un transportista existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transportista actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = TransportistaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Transportista no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<TransportistaResponseDTO> actualizarTransportista(
            @Parameter(description = "ID del transportista") @PathVariable Long id,
            @Valid @RequestBody TransportistaRequestDTO requestDTO) {

        TransportistaResponseDTO response = transportistaService.actualizarTransportista(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    // ====================================================================================
    // ELIMINAR TRANSPORTISTA
    // ====================================================================================
    @Operation(
            summary = "Eliminar un transportista por ID",
            description = "Elimina un transportista del sistema. Solo el rol ADMINISTRADOR puede realizar esta operación."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transportista eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Transportista no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarTransportista(
            @Parameter(description = "ID del transportista") @PathVariable Long id) {

        transportistaService.eliminarTransportista(id);
        return ResponseEntity.noContent().build();
    }
}
