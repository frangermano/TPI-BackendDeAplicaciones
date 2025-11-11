package back.tpi.ms_GestionDeInfraestructura.client;

import back.tpi.ms_GestionDeInfraestructura.dto.DistanciaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OsrmClient {
    private final RestClient restClient;

    @Value("${osrm.base-url}")
    private String osrmServiceUrl;


    /**
     * Calcula distancia (km) y tiempo (horas) entre dos puntos usando OSRM (perfil driving).
     */
    public DistanciaResponse calcularDistancia(double origenLat, double origenLon, double destinoLat, double destinoLon) {
        // OSRM: /route/v1/driving/{lon1},{lat1};{lon2},{lat2}
        String coords = String.format("%f,%f;%f,%f", origenLon, origenLat, destinoLon, destinoLat);
        String path = String.format("/route/v1/driving/%s?overview=false&alternatives=false&annotations=false", coords);

        try {
            return restClient.get()
                    .uri(osrmServiceUrl + path)
                    .retrieve()
                    .body(DistanciaResponse.class);
        } catch (Exception e) {
            log.error("Error al crear contenedor: {}", e.getMessage());
            throw new RuntimeException("Error al crear contenedor en ms-GestionDeInfraestructura: " + e.getMessage());
        }

    }

}
