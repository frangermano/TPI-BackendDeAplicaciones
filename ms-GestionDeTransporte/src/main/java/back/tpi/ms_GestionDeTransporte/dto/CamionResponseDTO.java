package back.tpi.ms_GestionDeTransporte.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta que representa la información completa de un camión")
public class CamionResponseDTO {

    @Schema(
            description = "Patente del camión",
            example = "AB123CD"
    )
    private String patente;

    @Schema(
            description = "Costo del combustible por unidad",
            example = "550.75"
    )
    private Double costoCombustible;

    @Schema(
            description = "Costo por kilómetro recorrido",
            example = "120.50"
    )
    private Double costoKm;

    @Schema(
            description = "Indica si el camión está disponible",
            example = "true"
    )
    private Boolean disponible;

    @Schema(
            description = "Transportista asignado al camión"
    )
    private TransportistaResponseDTO transportista;

    @Schema(
            description = "Tipo de camión asociado"
    )
    private TipoCamionDTO tipoCamion;
}