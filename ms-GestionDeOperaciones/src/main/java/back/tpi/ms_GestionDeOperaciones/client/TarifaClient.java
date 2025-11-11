package back.tpi.ms_GestionDeOperaciones.client;

import back.tpi.ms_GestionDeOperaciones.dto.DistanciaResponse;
import back.tpi.ms_GestionDeOperaciones.dto.TarifaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
            return restClient.post()
                    .uri(tarifasServiceUrl + "/api/tarifas")
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
            Boolean existe = restClient.get()
                    .uri(tarifasServiceUrl + "/api/tarifas/{id}/existe", tarifaId)
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
            return restClient.get()
                    .uri(tarifasServiceUrl + "/api/tarifas/{id}", tarifaId)
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
            return restClient.get()
                    .uri(tarifasServiceUrl + "/api/tarifas/{id}/calcular-costo-estimado?distancia={distancia}",
                            tarifaId, distancia)
                    .retrieve()
                    .body(Double.class);
        } catch (Exception e) {
            log.error("Error al calcular costo: {}", e.getMessage());
            throw new RuntimeException("Error al calcular costo: " + e.getMessage());
        }
    }
}