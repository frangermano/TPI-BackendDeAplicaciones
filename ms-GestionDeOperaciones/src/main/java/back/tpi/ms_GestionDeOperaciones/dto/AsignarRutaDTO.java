package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * DTO para asignar una ruta completa con sus tramos a una solicitud
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO utilizado para asignar una ruta completa (con sus tramos) a una solicitud de traslado")
public class AsignarRutaDTO {

    @Schema(
            description = "ID de la solicitud de traslado a la cual se le quiere asignar la ruta",
            example = "42",
            required = true
    )
    private Long solicitudTrasladoId;

    @Schema(
            description = "Lista de tramos que componen la ruta. Cada tramo incluye información como ciudad origen, destino, distancia, etc.",
            required = true
    )
    private List<TramoDTO> tramos;

    @Schema(
            description = "Si está en true, el sistema calculará automáticamente la ruta sin necesidad de especificar los tramos",
            example = "false"
    )
    private Boolean calcularAutomaticamente;
}