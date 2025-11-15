package back.tpi.ms_GestionDeTransporte.controller;

import back.tpi.ms_GestionDeTransporte.dto.CamionRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.CamionResponseDTO;
import back.tpi.ms_GestionDeTransporte.service.CamionService;
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

@Tag(name = "Camiones", description = "Operaciones relacionadas con camiones")
@RestController
@RequestMapping("/api/camiones")
@RequiredArgsConstructor
public class CamionController {

    private final CamionService camionService;

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Registrar un nuevo camión",
            description = "Crea un nuevo camión en el sistema. Solo accesible para administradores."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Camión registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = CamionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CamionResponseDTO> registrarCamion(
            @Valid @RequestBody CamionRequestDTO requestDTO) {
        CamionResponseDTO response = camionService.registrarCamion(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Obtener camión por patente",
            description = "Devuelve la información de un camión según su patente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Camión encontrado",
                    content = @Content(schema = @Schema(implementation = CamionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Camión no encontrado")
    })
    @GetMapping("/{patente}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<CamionResponseDTO> obtenerCamion(
            @Parameter(description = "Patente del camión a buscar", example = "AB123CD")
            @PathVariable String patente) {
        CamionResponseDTO response = camionService.obtenerCamionPorPatente(patente);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Obtener todos los camiones",
            description = "Retorna una lista con todos los camiones registrados."
    )
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<List<CamionResponseDTO>> obtenerTodosLosCamiones() {
        List<CamionResponseDTO> response = camionService.obtenerTodosLosCamiones();
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Obtener camiones disponibles",
            description = "Devuelve todos los camiones que actualmente están disponibles."
    )
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<List<CamionResponseDTO>> obtenerCamionesDisponibles() {
        List<CamionResponseDTO> response = camionService.obtenerCamionesDisponibles();
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Obtener camiones asignados a un transportista",
            description = "Retorna todos los camiones que pertenecen al transportista indicado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Transportista no encontrado")
    })
    @GetMapping("/transportista/{transportistaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<List<CamionResponseDTO>> obtenerCamionesPorTransportista(
            @Parameter(description = "ID del transportista", example = "12")
            @PathVariable Long transportistaId) {
        List<CamionResponseDTO> response =
                camionService.obtenerCamionesPorTransportista(transportistaId);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Actualizar la disponibilidad de un camión",
            description = "Actualiza el estado de disponibilidad de un camión."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Disponibilidad actualizada"),
            @ApiResponse(responseCode = "404", description = "Camión no encontrado")
    })
    @PutMapping("/{patente}/disponibilidad")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<Void> actualizarDisponibilidad(
            @Parameter(description = "Patente del camión", example = "AC456BD")
            @PathVariable String patente,
            @Parameter(description = "Nuevo estado de disponibilidad")
            @RequestParam Boolean disponible) {

        camionService.actualizarDisponibilidad(patente, disponible);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Actualizar un camión",
            description = "Permite modificar todos los datos de un camión existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Camión actualizado",
                    content = @Content(schema = @Schema(implementation = CamionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Camión no encontrado")
    })
    @PutMapping("/{patente}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CamionResponseDTO> actualizarCamion(
            @Parameter(description = "Patente del camión a actualizar")
            @PathVariable String patente,
            @Valid @RequestBody CamionRequestDTO requestDTO) {

        CamionResponseDTO response = camionService.actualizarCamion(patente, requestDTO);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Eliminar un camión",
            description = "Elimina un camión según su patente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Camión eliminado"),
            @ApiResponse(responseCode = "404", description = "Camión no encontrado")
    })
    @DeleteMapping("/{patente}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarCamion(
            @Parameter(description = "Patente del camión a eliminar")
            @PathVariable String patente) {

        camionService.eliminarCamion(patente);
        return ResponseEntity.noContent().build();
    }
}
