package back.tpi.ms_GestionDeOperaciones.client;

import back.tpi.ms_GestionDeOperaciones.dto.TarifaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class TarifaClient {

    private final RestClient restClient;

    @Value("${microservices.gestion-tarifas.url:http://ms-gestiondeinfraestructura:8082}")
    private String tarifasServiceUrl;

    /**
     * Crea una nueva tarifa en el microservicio de tarifas
     */
    public TarifaDTO crearTarifa(TarifaDTO tarifaDTO) {
        try {
            log.info("Creando tarifa en ms-GestionDeCostosYTarifas");

            // 👇 OBTENER Y AGREGAR EL TOKEN
            String token = obtenerTokenActual();

            return restClient.post()
                    .uri(tarifasServiceUrl + "/api/tarifas")
                    .header("Authorization", "Bearer " + token)  // 👈 AGREGAR TOKEN
                    .body(tarifaDTO)
                    .retrieve()
                    .body(TarifaDTO.class);
        } catch (Exception e) {
            log.error("Error al crear tarifa: {}", e.getMessage());
            throw new RuntimeException("Error al crear tarifa en ms-GestionDeCostosYTarifas: " + e.getMessage());
        }
    }

    /**
     * Verifica si existe una tarifa
     */
    public boolean existeTarifa(Long tarifaId) {
        try {
            String token = obtenerTokenActual();

            Boolean existe = restClient.get()
                    .uri(tarifasServiceUrl + "/api/tarifas/{id}/existe", tarifaId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Boolean.class);

            return existe != null && existe;
        } catch (Exception e) {
            log.error("Error al verificar tarifa: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene una tarifa por su ID
     */
    public TarifaDTO getTarifa(Long tarifaId) {
        try {
            String token = obtenerTokenActual();

            return restClient.get()
                    .uri(tarifasServiceUrl + "/api/tarifas/{id}", tarifaId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(TarifaDTO.class);
        } catch (Exception e) {
            log.error("Error al obtener tarifa: {}", e.getMessage());
            throw new RuntimeException("Error al obtener tarifa: " + e.getMessage());
        }
    }

    /**
     * Calcula el costo estimado según la tarifa y distancia
     */
    public Double calcularCostoEstimado(Long tarifaId, double distancia) {
        try {
            String token = obtenerTokenActual();

            return restClient.get()
                    .uri(tarifasServiceUrl + "/api/tarifas/{id}/calcular-costo-estimado?distancia={distancia}",
                            tarifaId, distancia)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Double.class);
        } catch (Exception e) {
            log.error("Error al calcular costo: {}", e.getMessage());
            throw new RuntimeException("Error al calcular costo: " + e.getMessage());
        }
    }

    public Double calcularCostoReal(Long tarifaId, double pesoContendor,
                                    double volumenContenedor, double costoBaseTotal) {
        try {
            String token = obtenerTokenActual();

            return restClient.get()
                    .uri(tarifasServiceUrl + "/api/tarifas/{id}/calcular-costo-real",
                            tarifaId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Double.class);
        } catch (Exception e) {
            log.error("Error al calcular costo: {}", e.getMessage());
            throw new RuntimeException("Error al calcular costo: " + e.getMessage());
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