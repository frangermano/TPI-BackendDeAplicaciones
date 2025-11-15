package back.tpi.ms_GestionDeOperaciones.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Información detallada del cálculo de costos asociado a una solicitud.")
public class CostoDetalleDTO {

    @Schema(
            description = "ID de la solicitud a la que pertenece el cálculo de costos.",
            example = "1502"
    )
    private Long solicitudId;

    // ===================== DETALLES DEL CÁLCULO =====================

    @Schema(
            description = "Distancia total recorrida por la ruta en kilómetros.",
            example = "325.7"
    )
    private Double distanciaTotal;      // km

    @Schema(
            description = "Cantidad total de horas de estadía asociadas a la solicitud.",
            example = "3.5"
    )
    private Double horasEstadia;        // horas

    @Schema(
            description = "Peso del contenedor en kilogramos.",
            example = "820.0"
    )
    private Double pesoContenedor;      // kg

    @Schema(
            description = "Volumen del contenedor en metros cúbicos.",
            example = "12.5"
    )
    private Double volumenContenedor;   // m³

    // ===================== COSTO =====================

    @Schema(
            description = "Costo total calculado para la solicitud.",
            example = "45800.75"
    )
    private Double costoTotal;

    // ===================== INFORMACIÓN ADICIONAL =====================

    @Schema(
            description = "Nombre de la tarifa utilizada para el cálculo.",
            example = "Tarifa Estándar Nacional"
    )
    private String nombreTarifa;

    @Schema(
            description = "ID de la tarifa utilizada.",
            example = "3"
    )
    private Long tarifaId;
}