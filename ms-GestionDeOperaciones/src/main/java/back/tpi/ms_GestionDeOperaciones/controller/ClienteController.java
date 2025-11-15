package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.domain.Cliente;
import back.tpi.ms_GestionDeOperaciones.dto.ClienteDTO;
import back.tpi.ms_GestionDeOperaciones.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cliente", description = "Operaciones relacionadas con los clientes del sistema")
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    // ============================================================================
    // 1) CREAR CLIENTE
    // ============================================================================

    @Operation(
            summary = "Crear un nuevo cliente",
            description = "Permite crear un cliente proporcionando nombre, email y demás información.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para la creación de un cliente",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ClienteDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo creación cliente",
                                    value = """
                                            {
                                                "nombre": "Juan Pérez",
                                                "email": "juanperez@example.com",
                                                "telefono": "1144556677"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado — solo ADMINISTRADOR y CLIENTE"),
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<Cliente> crearCliente(@RequestBody ClienteDTO clienteDTO) {
        try {
            Cliente nuevoCliente = service.crearCliente(clienteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // ============================================================================
    // 2) OBTENER TODOS
    // ============================================================================

    @Operation(
            summary = "Obtener todos los clientes",
            description = "Devuelve el listado completo de clientes registrados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRADOR")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }


    // ============================================================================
    // 3) OBTENER POR ID
    // ============================================================================

    @Operation(
            summary = "Buscar cliente por ID",
            description = "Devuelve un cliente específico según su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "No se encontró el cliente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Cliente> obtenerPorId(
            @Parameter(description = "ID del cliente", example = "10")
            @PathVariable Long id) {

        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // ============================================================================
    // 4) OBTENER POR EMAIL
    // ============================================================================

    @Operation(
            summary = "Buscar cliente por email",
            description = "Consulta un cliente utilizando su correo electrónico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe cliente para ese email"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<Cliente> buscarPorEmail(
            @Parameter(description = "Correo electrónico del cliente", example = "cliente@mail.com")
            @PathVariable String email) {

        return service.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // ============================================================================
    // 5) ACTUALIZAR CLIENTE
    // ============================================================================

    @Operation(
            summary = "Actualizar un cliente",
            description = "Permite actualizar los datos de un cliente existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<Cliente> actualizarCliente(
            @Parameter(description = "ID del cliente a actualizar", example = "4")
            @PathVariable Long id,
            @RequestBody ClienteDTO clienteDTO) {

        try {
            return ResponseEntity.ok(service.actualizarCliente(id, clienteDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // ============================================================================
    // 6) ELIMINAR CLIENTE
    // ============================================================================

    @Operation(
            summary = "Eliminar un cliente",
            description = "Elimina un cliente existente según su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRADOR")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarCliente(
            @Parameter(description = "ID del cliente", example = "3")
            @PathVariable Long id) {

        try {
            service.eliminarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}