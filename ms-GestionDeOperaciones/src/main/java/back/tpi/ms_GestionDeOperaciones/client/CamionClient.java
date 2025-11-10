package back.tpi.ms_GestionDeOperaciones.client;

import back.tpi.ms_GestionDeOperaciones.dto.CamionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CamionClient {

    private final RestClient restClient;

    @Value("${microservices.gestion-transportes.url:http://ms-gestiondetransporte:8084}")
    private String camionServiceUrl;


    /**
     * Obtiene un camión por su patente
     */
    public CamionDTO obtenerCamionPorPatente(String patente) {
        return restClient.get()
                .uri(camionServiceUrl + "/api/camiones/{patente}", patente)
                .retrieve()
                .body(CamionDTO.class);
    }

    /**
     * Obtiene todos los camiones disponibles
     */
    public List<CamionDTO> obtenerCamionesDisponibles() {
        return restClient.get()
                .uri("/disponibles")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CamionDTO>>() {});
    }

    /**
     * Marca un camión como no disponible (asignado)
     */
    public CamionDTO actualizarDisponibilidad(String patente, Boolean disponible) {
        return restClient.patch()
                .uri("/{patente}/disponibilidad?disponible={disponible}", patente, disponible)
                .retrieve()
                .body(CamionDTO.class);
    }
}

