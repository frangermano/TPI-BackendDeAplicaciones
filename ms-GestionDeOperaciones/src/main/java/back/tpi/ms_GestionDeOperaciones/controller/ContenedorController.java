package back.tpi.ms_GestionDeOperaciones.controller;

import back.tpi.ms_GestionDeOperaciones.domain.Contenedor;
import back.tpi.ms_GestionDeOperaciones.dto.ContenedorDTO;
import back.tpi.ms_GestionDeOperaciones.service.ContenedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenedores")
@RequiredArgsConstructor
public class ContenedorController {

    private final ContenedorService service;

    @GetMapping
    public ResponseEntity<List<Contenedor>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Contenedor>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.obtenerPorCliente(clienteId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contenedor> actualizarContenedor(
            @PathVariable Long id,
            @RequestBody ContenedorDTO contenedorDTO) {
        try {
            return ResponseEntity.ok(service.actualizarContenedor(id, contenedorDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContenedor(@PathVariable Long id) {
        try {
            service.eliminarContenedor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}