package back.tpi.ms_GestionDeOperaciones.client;

import back.tpi.ms_GestionDeOperaciones.dto.ContenedorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContenedorClient {

    private final RestClient restClient;

    @Value("${microservices.gestion-infraestructura.url:http://localhost:8082}")
    private String infraestructuraServiceUrl;

    /**
     * Crea un nuevo contenedor en el microservicio de infraestructura
     */
    public ContenedorDTO crearContenedor(ContenedorDTO contenedorDTO) {
        try {
            return restClient.post()
                    .uri(infraestructuraServiceUrl + "/api/contenedores")
                    .body(contenedorDTO)
                    .retrieve()
                    .body(ContenedorDTO.class);
        } catch (Exception e) {
            log.error("Error al crear contenedor: {}", e.getMessage());
            throw new RuntimeException("Error al crear contenedor en ms-GestionDeInfraestructura: " + e.getMessage());
        }
    }

    /**
     * Verifica si existe un contenedor
     */
    public boolean existeContenedor(Long contenedorId) {
        try {
            Boolean existe = restClient.get()
                    .uri(infraestructuraServiceUrl + "/api/contenedores/{id}/existe", contenedorId)
                    .retrieve()
                    .body(Boolean.class);

            return existe != null && existe;
        } catch (Exception e) {
            log.error("Error al verificar contenedor: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene un contenedor por su ID
     */
    public ContenedorDTO getContenedor(Long contenedorId) {
        try {
            return restClient.get()
                    .uri(infraestructuraServiceUrl + "/api/contenedores/{id}", contenedorId)
                    .retrieve()
                    .body(ContenedorDTO.class);
        } catch (Exception e) {
            log.error("Error al obtener contenedor: {}", e.getMessage());
            throw new RuntimeException("Error al obtener contenedor: " + e.getMessage());
        }
    }
}