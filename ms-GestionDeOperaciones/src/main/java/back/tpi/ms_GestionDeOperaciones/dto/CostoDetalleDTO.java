package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostoDetalleDTO {

    // Detalles del cálculo
    private double distanciaTotal;      // km
    private double horasEstadia;        // horas
    private double pesoContenedor;      // kg
    private double volumenContenedor;   // m³

    // Costos parciales
    private double costoCombustible;
    private double costoEstadia;
    private double costoPeso;
    private double costoVolumen;
    private double cargoGestion;

    // Costo total
    private double costoTotal;

    // Información adicional
    private String nombreTarifa;
    private Long tarifaId;
}