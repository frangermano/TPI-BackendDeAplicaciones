package back.tpi.ms_GestionDeOperaciones.client;

import back.tpi.ms_GestionDeOperaciones.dto.DistanciaResponse;
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
    public DistanciaResponse calcularDistancia(double origenLat, double origenLon,
                                               double destinoLat, double destinoLon) {
        String coords = String.format("%f,%f;%f,%f", origenLon, origenLat, destinoLon, destinoLat);
        String path = String.format("/route/v1/driving/%s?overview=false&alternatives=false&annotations=false", coords);

        int maxRetries = 3;
        int retryDelay = 1000; // 1 segundo

        for (int i = 0; i < maxRetries; i++) {
            try {
                log.info("Intentando calcular distancia con OSRM (intento {}/{})", i + 1, maxRetries);

                DistanciaResponse response = restClient.get()
                        .uri(osrmServiceUrl + path)
                        .retrieve()
                        .body(DistanciaResponse.class);

                log.info("✅ Distancia calculada exitosamente");
                return response;

            } catch (Exception e) {
                log.warn("⚠️ Error en intento {}/{}: {}", i + 1, maxRetries, e.getMessage());

                if (i == maxRetries - 1) {
                    // Último intento falló
                    log.error("❌ Todos los intentos fallaron. Usando distancia aproximada.");
                    return crearDistanciaAproximada(origenLat, origenLon, destinoLat, destinoLon);
                }

                // Esperar antes del siguiente intento
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Fallback (nunca debería llegar aquí)
        return crearDistanciaAproximada(origenLat, origenLon, destinoLat, destinoLon);
    }

    /**
     * Crea una respuesta de distancia aproximada usando Haversine cuando OSRM falla
     */
    private DistanciaResponse crearDistanciaAproximada(double lat1, double lon1,
                                                       double lat2, double lon2) {
        // Calcular distancia en línea recta (Haversine)
        double distanciaKm = calcularDistanciaHaversine(lat1, lon1, lat2, lon2);

        // Estimar tiempo asumiendo 60 km/h promedio
        double tiempoHoras = distanciaKm / 60.0;

        log.info("📏 Distancia aproximada calculada: {} km, Tiempo estimado: {} hs",
                distanciaKm, tiempoHoras);

        DistanciaResponse response = new DistanciaResponse();
        DistanciaResponse.Route route = new DistanciaResponse.Route();
        route.setDistance(distanciaKm * 1000); // metros
        route.setDuration(tiempoHoras * 3600);  // segundos

        response.setRoutes(List.of(route));
        return response;
    }

    /**
     * Fórmula de Haversine para calcular distancia en línea recta
     */
    private double calcularDistanciaHaversine(double lat1, double lon1,
                                              double lat2, double lon2) {
        final int RADIO_TIERRA_KM = 6371;

        double latDistancia = Math.toRadians(lat2 - lat1);
        double lonDistancia = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistancia / 2) * Math.sin(latDistancia / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistancia / 2) * Math.sin(lonDistancia / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_KM * c;
    }

}
