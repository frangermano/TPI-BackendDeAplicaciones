package back.tpi.ms_GestionDeTransporte.controller;

import back.tpi.ms_GestionDeTransporte.domain.Contenedor;
import back.tpi.ms_GestionDeTransporte.service.ContenedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contenedores")
@RequiredArgsConstructor
public class ContenedorController {

    private final ContenedorService service;

    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeContenedor(@PathVariable Long id) {
        return ResponseEntity.ok(service.existeContenedor(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> obtenerContenedor(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}