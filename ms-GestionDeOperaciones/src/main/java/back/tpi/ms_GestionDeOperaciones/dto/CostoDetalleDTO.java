package back.tpi.ms_GestionDeOperaciones.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CostoDetalleDTO {

    private Long solicitudId;

    // Detalles del cálculo
    private double distanciaTotal;      // km
    private double horasEstadia;        // horas
    private double pesoContenedor;      // kg
    private double volumenContenedor;   // m³

    // Costo total
    private double costoTotal;

    // Información adicional
    private String nombreTarifa;
    private Long tarifaId;
}