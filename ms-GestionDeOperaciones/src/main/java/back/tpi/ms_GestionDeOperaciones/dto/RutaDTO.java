package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "RutaDTO",
        description = "Representa una ruta generada para una solicitud de traslado. Incluye la cantidad de tramos, depósitos utilizados y el detalle completo de cada tramo."
)
public class RutaDTO {

    @Schema(
            description = "ID único de la ruta generada",
            example = "45"
    )
    private Long rutaId;

    @Schema(
            description = "ID de la solicitud de traslado asociada a esta ruta",
            example = "1203"
    )
    private Long solicitudTrasladoId;

    @Schema(
            description = "Cantidad total de tramos (segmentos del recorrido)",
            example = "3"
    )
    private Integer cantidadTramos;

    @Schema(
            description = "Cantidad total de depósitos intermedios utilizados en la ruta",
            example = "2"
    )
    private Integer cantidadDepositos;

    @Schema(
            description = "Listado de tramos de la ruta, incluyendo origen, destino, distancias y detalles",
            implementation = TramoDTO.class
    )
    private List<TramoDTO> tramos;
}