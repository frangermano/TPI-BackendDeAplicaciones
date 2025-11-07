package back.tpi.ms_GestionDeOperaciones.client;

import back.tpi.ms_GestionDeOperaciones.dto.CamionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Cliente Feign para comunicarse con el microservicio de Gestión de Transporte
 */
@FeignClient(name = "ms-GestionDeTransporte", url = "${microservices.transporte.url:http://localhost:8082}")
public interface CamionClient {

    /**
     * Obtiene un camión por su patente
     */
    @GetMapping("/api/camiones/{patente}")
    CamionDTO obtenerCamionPorPatente(@PathVariable String patente);

    /**
     * Obtiene todos los camiones disponibles
     */
    @GetMapping("/api/camiones/disponibles")
    List<CamionDTO> obtenerCamionesDisponibles();

    /**
     * Marca un camión como no disponible (asignado)
     */
    @PatchMapping("/api/camiones/{patente}/disponibilidad")
    CamionDTO actualizarDisponibilidad(
            @PathVariable String patente,
            @RequestParam Boolean disponible);
}


