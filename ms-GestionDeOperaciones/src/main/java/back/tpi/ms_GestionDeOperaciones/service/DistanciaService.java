package back.tpi.ms_GestionDeOperaciones.service;
import back.tpi.ms_GestionDeOperaciones.dto.DistanciaResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class DistanciaService {

    private final RestClient osrmClient;

    // Inyectá el bean que ya tienen configurado
    public DistanciaService(RestClient osrmClient) {
        this.osrmClient = osrmClient;
    }

    /**
     * Calcula distancia (km) y tiempo (horas) entre dos puntos usando OSRM (perfil driving).
     */
    public DistanciaResponse calcularDistancia(double origenLat, double origenLon, double destinoLat, double destinoLon) {
        // OSRM: /route/v1/driving/{lon1},{lat1};{lon2},{lat2}
        String coords = String.format("%f,%f;%f,%f", origenLon, origenLat, destinoLon, destinoLat);
        String path = String.format("/route/v1/driving/%s?overview=false&alternatives=false&annotations=false", coords);

        OsrmRouteResponse resp = osrmClient.get()
                .uri(path)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            throw new RuntimeException("Error al consultar OSRM: " + response.getStatusCode());
                        }
                )
                .body(OsrmRouteResponse.class);

        if (resp == null || resp.routes == null || resp.routes.isEmpty()) {
            throw new RuntimeException("OSRM no devolvió rutas");
        }

        Route r = resp.routes.get(0);
        double km = r.distance / 1000.0;   // metros -> km
        double horas = r.duration / 3600.0; // segundos -> horas
        return new DistanciaResponse(km, horas);
    }

    // --- DTOs mínimos para parsear OSRM ---
    public static class OsrmRouteResponse {
        public String code;
        public List<Route> routes;
        public String message;
    }

    public static class Route {
        public double distance; // meters
        public double duration; // seconds
    }
}