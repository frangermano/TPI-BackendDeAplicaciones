package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.Data;
import java.util.List;

@Data
public class DistanciaResponse {
    private List<Route> routes;

    @Data
    public static class Route {
        private double distance; // en metros
        private double duration; // en segundos
    }

    // 🔹 Devuelve la distancia en kilómetros, redondeada a 2 decimales
    public double getDistanciaKm() {
        if (routes != null && !routes.isEmpty()) {
            double distanciaKm = routes.get(0).getDistance() / 1000.0;
            return Math.round(distanciaKm * 100.0) / 100.0; // Redondea a 2 decimales
        }
        return 0.0;
    }

    // 🔹 Tiempo numérico (en horas decimales)
    public double getTiempoHoras() {
        return routes != null && !routes.isEmpty()
                ? routes.get(0).getDuration() / 3600.0
                : 0.0;
    }

    // 🔹 Distancia legible (siempre en km, redondeada a 2 decimales)
    public String getDistanciaLegible() {
        return String.format("%.2f km", getDistanciaKm());
    }

    // 🔹 Tiempo legible (formato hh:mm hs)
    public String getTiempoLegible() {
        double tiempoHoras = getTiempoHoras();
        int horas = (int) tiempoHoras;
        int minutos = (int) Math.round((tiempoHoras - horas) * 60);
        return String.format("%d:%02d hs", horas, minutos);
    }
}
