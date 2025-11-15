package back.tpi.ms_GestionDeOperaciones.client;

import back.tpi.ms_GestionDeOperaciones.dto.CamionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
            String token = obtenerTokenActual();

            return restClient.get()
                    .uri(camionServiceUrl + "/api/camiones/{patente}", patente)
                    .header("Authorization", "Bearer " + token)
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
            String token = obtenerTokenActual();

            return restClient.get()
                    .uri(camionServiceUrl + "/api/camiones/disponibles")
                    .header("Authorization", "Bearer " + token)
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
            String token = obtenerTokenActual();

            restClient.put()
                    .uri(camionServiceUrl + "/api/camiones/{patente}/disponibilidad?disponible={disponible}",
                            patente, disponible)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
            log.info("✅ Disponibilidad actualizada exitosamente para camión {}", patente);
        } catch (Exception e) {
            log.error("Error al actualizar disponibilidad del camión {}: {}", patente, e.getMessage());
            throw new RuntimeException("Error al actualizar disponibilidad del camión: " + e.getMessage());
        }
    }

    /**
     * Obtiene el token JWT del contexto de seguridad actual
     */
    private String obtenerTokenActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            log.debug("Token JWT obtenido del contexto de seguridad");
            return jwt.getTokenValue();
        }

        log.error("No se pudo obtener el token JWT del contexto de seguridad");
        throw new RuntimeException("Usuario no autenticado - No hay token disponible");
    }
}