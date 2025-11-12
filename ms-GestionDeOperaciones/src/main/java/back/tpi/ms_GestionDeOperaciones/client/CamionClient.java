package back.tpi.ms_GestionDeOperaciones.client;

import back.tpi.ms_GestionDeOperaciones.dto.CamionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CamionClient {

    private final RestClient restClient;

    @Value("${microservices.gestion-transportes.url:http://ms-gestiondetransporte:8084}")
    private String camionServiceUrl;


    /**
     * Obtiene un camión por su patente
     */
    public CamionDTO obtenerCamionPorPatente(String patente) {
        try {
            log.info("Consultando camión con patente: {}", patente);
            return restClient.get()
                    .uri(camionServiceUrl + "/api/camiones/{patente}", patente)
                    .retrieve()
                    .body(CamionDTO.class);
        } catch (Exception e) {
            log.error("Error al obtener camión {}: {}", patente, e.getMessage());
            throw new RuntimeException("Error al obtener información del camión: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los camiones disponibles
     */
    public List<CamionDTO> obtenerCamionesDisponibles() {
        try {
            log.info("Consultando camiones disponibles");
            return restClient.get()
                    .uri(camionServiceUrl + "/api/camiones/disponibles")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CamionDTO>>() {});
        } catch (Exception e) {
            log.error("Error al obtener camiones disponibles: {}", e.getMessage());
            throw new RuntimeException("Error al obtener camiones disponibles: " + e.getMessage());
        }
    }

    /**
     * Marca un camión como no disponible (asignado)
     */
    public void actualizarDisponibilidad(String patente, Boolean disponible) {
        try {
            log.info("Actualizando disponibilidad del camión {} a: {}", patente, disponible);
            restClient.put()
                    .uri(camionServiceUrl + "/api/camiones/{patente}/disponibilidad?disponible={disponible}",
                            patente, disponible)
                    .retrieve()
                    .toBodilessEntity();
            log.info("✅ Disponibilidad actualizada exitosamente para camión {}", patente);
        } catch (Exception e) {
            log.error("Error al actualizar disponibilidad del camión {}: {}", patente, e.getMessage());
            throw new RuntimeException("Error al actualizar disponibilidad del camión: " + e.getMessage());
        }
    }
}

