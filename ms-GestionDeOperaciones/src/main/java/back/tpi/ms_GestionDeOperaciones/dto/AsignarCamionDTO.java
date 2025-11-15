package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO para asignar un camión a un tramo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO utilizado para asignar un camión a un tramo específico")
public class AsignarCamionDTO {

    @Schema(
            description = "ID del tramo al cual se desea asignar el camión",
            example = "150",
            required = true
    )
    private Long tramoId;

    @Schema(
            description = "Patente del camión que se va a asignar al tramo",
            example = "AB123CD",
            required = true
    )
    private String patenteCamion;

    @Schema(
            description = "Indica si se debe validar la capacidad del camión antes de asignarlo (default: true)",
            example = "true"
    )
    private Boolean validarCapacidad;
}