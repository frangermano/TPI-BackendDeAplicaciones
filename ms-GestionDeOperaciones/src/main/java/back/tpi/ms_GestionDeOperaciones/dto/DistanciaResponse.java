package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Data
@Schema(description = "Respuesta del servicio OSRM que contiene rutas y permite obtener distancia y tiempo de forma procesada")
public class DistanciaResponse {

    @Schema(
            description = "Lista de rutas encontradas por el motor de cálculo OSRM",
            example = "[{\"distance\": 12345.6, \"duration\": 850.3}]"
    )
    private List<Route> routes;

    @Data
    @Schema(description = "Información detallada de una ruta devuelta por OSRM")
    public static class Route {

        @Schema(
                description = "Distancia total de la ruta en metros",
                example = "12345.6"
        )
        private double distance;

        @Schema(
                description = "Tiempo total de la ruta en segundos",
                example = "850.3"
        )
        private double duration;
    }

    // --------------------------------------------
    // MÉTODOS CON VALORES CALCULADOS
    // --------------------------------------------

    @Schema(
            description = "Distancia de la ruta principal expresada en kilómetros (redondeada a 2 decimales)",
            example = "12.35"
    )
    public double getDistanciaKm() {
        if (routes != null && !routes.isEmpty()) {
            double distanciaKm = routes.get(0).getDistance() / 1000.0;
            return Math.round(distanciaKm * 100.0) / 100.0;
        }
        return 0.0;
    }

    @Schema(
            description = "Duración de la ruta principal expresada en horas (formato decimal)",
            example = "0.24"
    )
    public double getTiempoHoras() {
        return routes != null && !routes.isEmpty()
                ? routes.get(0).getDuration() / 3600.0
                : 0.0;
    }

    @Schema(
            description = "Distancia legible en formato 'X.XX km'",
            example = "12.35 km"
    )
    public String getDistanciaLegible() {
        return String.format("%.2f km", getDistanciaKm());
    }

    @Schema(
            description = "Tiempo legible en formato hh:mm hs",
            example = "0:14 hs"
    )
    public String getTiempoLegible() {
        double tiempoHoras = getTiempoHoras();
        int horas = (int) tiempoHoras;
        int minutos = (int) Math.round((tiempoHoras - horas) * 60);
        return String.format("%d:%02d hs", horas, minutos);
    }
}