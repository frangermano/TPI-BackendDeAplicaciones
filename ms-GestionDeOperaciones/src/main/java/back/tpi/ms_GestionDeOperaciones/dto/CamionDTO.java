package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO para representar un camión proveniente del microservicio de transporte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa un camión del microservicio de transporte")
public class CamionDTO {

    @Schema(
            description = "Patente del camión",
            example = "AB123CD",
            required = true
    )
    private String patente;

    @Schema(
            description = "Costo del combustible por kilómetro recorrido para este camión (en ARS/km)",
            example = "0.35"
    )
    private Double costoCombustible;

    @Schema(
            description = "Costo operativo por kilómetro recorrido (en ARS/km). Puede incluir mantenimiento, desgaste, etc.",
            example = "1.50"
    )
    private Double costoKm;

    @Schema(
            description = "Indica si el camión se encuentra disponible para asignación",
            example = "true"
    )
    private Boolean disponible;

    @Schema(
            description = "ID del transportista propietario o asignado al camión",
            example = "7"
    )
    private Long transportistaId;

    @Schema(
            description = "Tipo de camión, incluyendo información como capacidad máxima de peso y volumen"
    )
    private TipoCamionDTO tipoCamion;
}