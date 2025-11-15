package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.domain.Contenedor;
import back.tpi.ms_GestionDeOperaciones.dto.ContenedorDTO;
import back.tpi.ms_GestionDeOperaciones.service.ContenedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Contenedor", description = "Operaciones relacionadas con contenedores")
@RestController
@RequestMapping("/api/contenedores-operaciones")
@RequiredArgsConstructor
public class ContenedorController {

    private final ContenedorService service;

    // ============================================================
    // GET - Obtener todos
    // ============================================================

    @Operation(
            summary = "Obtener todos los contenedores",
            description = "Retorna una lista con todos los contenedores del sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Contenedor>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    // ============================================================
    // GET - Buscar por ID
    // ============================================================

    @Operation(
            summary = "Obtener contenedor por ID",
            description = "Retorna un contenedor según su ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID del contenedor", example = "10")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contenedor encontrado"),
                    @ApiResponse(responseCode = "404", description = "Contenedor no encontrado")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Contenedor> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================================================
    // GET - Buscar por cliente
    // ============================================================

    @Operation(
            summary = "Obtener contenedores por cliente",
            description = "Devuelve todos los contenedores asociados a un cliente.",
            parameters = {
                    @Parameter(name = "clienteId", description = "ID del cliente", example = "3")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contenedores obtenidos correctamente")
            }
    )
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<List<Contenedor>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.obtenerPorCliente(clienteId));
    }

    // ============================================================
    // PUT - Actualizar contenedor
    // ============================================================

    @Operation(
            summary = "Actualizar contenedor",
            description = "Actualiza los datos de un contenedor existente.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del contenedor a actualizar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ContenedorDTO.class))
            ),
            parameters = {
                    @Parameter(name = "id", description = "ID del contenedor a modificar", example = "8")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contenedor actualizado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Contenedor no encontrado")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Contenedor> actualizarContenedor(
            @PathVariable Long id,
            @RequestBody ContenedorDTO contenedorDTO) {

        try {
            return ResponseEntity.ok(service.actualizarContenedor(id, contenedorDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================================================
    // DELETE - Eliminar contenedor
    // ============================================================

    @Operation(
            summary = "Eliminar contenedor",
            description = "Elimina un contenedor según su ID.",
            parameters = {
                    @Parameter(name = "id", description = "ID del contenedor", example = "12")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Contenedor eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "No se encontró el contenedor")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarContenedor(@PathVariable Long id) {
        try {
            service.eliminarContenedor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}