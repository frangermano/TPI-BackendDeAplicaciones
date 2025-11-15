package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para representar un tipo de camión
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa un tipo de camión con sus capacidades básicas")
public class TipoCamionDTO {

    @Schema(
            description = "Identificador único del tipo de camión",
            example = "7"
    )
    private Long id;

    @Schema(
            description = "Nombre o descripción del tipo de camión",
            example = "Camión Semi Remolque"
    )
    private String nombre;

    @Schema(
            description = "Capacidad máxima de volumen (en m³)",
            example = "60.0"
    )
    private Double capacidadVolumen;

    @Schema(
            description = "Capacidad máxima de peso (en kg)",
            example = "28000.0"
    )
    private Double capacidadPeso;
}