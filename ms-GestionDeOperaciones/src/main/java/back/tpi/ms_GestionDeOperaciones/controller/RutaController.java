package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.dto.AsignarRutaDTO;
import back.tpi.ms_GestionDeOperaciones.dto.RutaDTO;
import back.tpi.ms_GestionDeOperaciones.dto.TramoDTO;
import back.tpi.ms_GestionDeOperaciones.service.RutaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
@Tag(
        name = "Ruta",
        description = "Operaciones relacionadas con rutas y tramos de una solicitud"
)
@SecurityRequirement(name = "bearer-jwt")
@SecurityRequirement(name = "keycloak-oauth2")
public class RutaController {

    private final RutaService rutaService;

    // =========================================================================
    // ASIGNAR RUTA COMPLETA
    // =========================================================================
    @Operation(
            summary = "Asignar una ruta con todos sus tramos",
            description = """
                    Permite asignar una ruta completa (con todos sus tramos) a una solicitud.

                    **Requiere rol:** ADMINISTRADOR
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para asignar la ruta",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AsignarRutaDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ruta creada correctamente",
                    content = @Content(schema = @Schema(implementation = RutaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos")
    })
    @PostMapping("/asignar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RutaDTO> asignarRutaConTramos(
            @RequestBody AsignarRutaDTO asignarRutaDTO
    ) {
        try {
            RutaDTO rutaCreada = rutaService.asignarRutaConTramos(asignarRutaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(rutaCreada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // =========================================================================
    // OBTENER RUTA POR SOLICITUD
    // =========================================================================
    @Operation(
            summary = "Obtener la ruta asignada a una solicitud",
            description = """
                    Devuelve la ruta completa asignada a una solicitud específica.

                    **Roles permitidos:** ADMINISTRADOR, TRANSPORTISTA, CLIENTE
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ruta encontrada",
                    content = @Content(schema = @Schema(implementation = RutaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud sin ruta o inexistente"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos")
    })
    @GetMapping("/solicitud/{solicitudId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA','CLIENTE')")
    public ResponseEntity<RutaDTO> obtenerRutaPorSolicitud(
            @Parameter(description = "ID de la solicitud", example = "10")
            @PathVariable Long solicitudId
    ) {
        try {
            RutaDTO ruta = rutaService.obtenerRutaPorSolicitud(solicitudId);
            return ResponseEntity.ok(ruta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================================
    // OBTENER TRAMOS DE UNA RUTA
    // =========================================================================
    @Operation(
            summary = "Obtener todos los tramos pertenecientes a una ruta",
            description = """
                    Devuelve todos los tramos de una ruta específica.

                    **Roles permitidos:** ADMINISTRADOR, TRANSPORTISTA
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tramos encontrados",
                    content = @Content(schema = @Schema(implementation = TramoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ruta no encontrada"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos")
    })
    @GetMapping("/{rutaId}/tramos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','TRANSPORTISTA')")
    public ResponseEntity<List<TramoDTO>> obtenerTramosPorRuta(
            @Parameter(description = "ID de la ruta", example = "3")
            @PathVariable Long rutaId
    ) {
        try {
            List<TramoDTO> tramos = rutaService.obtenerTramosPorRuta(rutaId);
            return ResponseEntity.ok(tramos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================================
    // ELIMINAR RUTA
    // =========================================================================
    @Operation(
            summary = "Eliminar una ruta y todos sus tramos",
            description = """
                    Elimina una ruta completa del sistema.

                    **Rol requerido:** ADMINISTRADOR
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ruta eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Ruta no encontrada"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos")
    })
    @DeleteMapping("/{rutaId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarRuta(
            @Parameter(description = "ID de la ruta a eliminar", example = "4")
            @PathVariable Long rutaId
    ) {
        try {
            rutaService.eliminarRuta(rutaId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
