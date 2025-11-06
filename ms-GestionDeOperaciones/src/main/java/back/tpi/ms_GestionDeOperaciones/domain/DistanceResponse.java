package back.tpi.ms_GestionDeOperaciones.domain;

public class DistanceResponse {
    private double distanciaKm;
    private double tiempoHoras;

    public DistanceResponse(double distanciaKm, double tiempoHoras) {
        this.distanciaKm = distanciaKm;
        this.tiempoHoras = tiempoHoras;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public double getTiempoHoras() {
        return tiempoHoras;
    }

    public void setTiempoHoras(double tiempoHoras) {
        this.tiempoHoras = tiempoHoras;
    }
}