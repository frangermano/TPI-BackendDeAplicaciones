package back.tpi.ms_GestionDeOperaciones.client;

import back.tpi.ms_GestionDeOperaciones.dto.DepositoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepositoClient {

    private final RestClient restClient;

    @Value("${microservices.gestion-infraestructura.url:http://ms-gestiondeinfraestructura:8082}")
    private String infraestructuraServiceUrl;

    /**
     * Obtiene depósitos que están en la ruta entre origen y destino
     */
    public List<DepositoDTO> obtenerDepositosEnRuta(Double latOrigen, Double lngOrigen,
                                                    Double latDestino, Double lngDestino,
                                                    Integer cantidad) {
        try {
            log.info("Consultando depósitos en ruta desde ({}, {}) hasta ({}, {})",
                    latOrigen, lngOrigen, latDestino, lngDestino);

            List<DepositoDTO> depositos = restClient.get()
                    .uri(infraestructuraServiceUrl + "/api/depositos/en-ruta?latOrigen={latO}&lngOrigen={lngO}&latDestino={latD}&lngDestino={lngD}&cantidad={cant}",
                            latOrigen, lngOrigen, latDestino, lngDestino, cantidad)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DepositoDTO>>() {});

            log.info("✅ Obtenidos {} depósitos en ruta", depositos != null ? depositos.size() : 0);
            return depositos != null ? depositos : List.of();

        } catch (Exception e) {
            log.error("❌ Error al obtener depósitos en ruta: {}", e.getMessage());
            // Retornar lista vacía en caso de error para que el flujo continúe
            return List.of();
        }
    }

    /**
     * Obtiene todos los depósitos activos
     */
    public List<DepositoDTO> obtenerDepositosActivos() {
        try {
            log.info("Consultando depósitos activos");

            List<DepositoDTO> depositos = restClient.get()
                    .uri(infraestructuraServiceUrl + "/api/depositos")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DepositoDTO>>() {});

            log.info("✅ Obtenidos {} depósitos activos", depositos != null ? depositos.size() : 0);
            return depositos != null ? depositos : List.of();

        } catch (Exception e) {
            log.error("❌ Error al obtener depósitos activos: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene depósitos cercanos a un punto específico
     */
    public List<DepositoDTO> obtenerDepositosCercanos(Double lat, Double lng, Double radioKm) {
        try {
            log.info("Consultando depósitos cercanos a ({}, {}) en radio de {} km",
                    lat, lng, radioKm);

            List<DepositoDTO> depositos = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(infraestructuraServiceUrl + "/api/depositos/cercanos")
                            .queryParam("lat", lat)
                            .queryParam("lng", lng)
                            .queryParam("radioKm", radioKm)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DepositoDTO>>() {});

            log.info("✅ Obtenidos {} depósitos cercanos", depositos != null ? depositos.size() : 0);
            return depositos != null ? depositos : List.of();

        } catch (Exception e) {
            log.error("❌ Error al obtener depósitos cercanos: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene un depósito por su ID
     */
    public DepositoDTO obtenerDepositoPorId(Long depositoId) {
        try {
            log.info("Consultando depósito con ID: {}", depositoId);

            DepositoDTO deposito = restClient.get()
                    .uri(infraestructuraServiceUrl + "/api/depositos/{id}", depositoId)
                    .retrieve()
                    .body(DepositoDTO.class);

            log.info("✅ Depósito obtenido: {}", deposito != null ? deposito.getNombre() : "null");
            return deposito;

        } catch (Exception e) {
            log.error("❌ Error al obtener depósito {}: {}", depositoId, e.getMessage());
            throw new RuntimeException("Error al obtener depósito: " + e.getMessage());
        }
    }

    /**
     * Verifica si existe un depósito
     */
    public boolean existeDeposito(Long depositoId) {
        try {
            Boolean existe = restClient.get()
                    .uri(infraestructuraServiceUrl + "/api/depositos/{id}/existe", depositoId)
                    .retrieve()
                    .body(Boolean.class);

            return existe != null && existe;
        } catch (Exception e) {
            log.error("❌ Error al verificar depósito: {}", e.getMessage());
            return false;
        }
    }
}