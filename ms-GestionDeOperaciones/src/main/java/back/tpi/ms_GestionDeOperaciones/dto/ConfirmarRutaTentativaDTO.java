package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO para confirmar y asignar una ruta tentativa seleccionada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO utilizado para confirmar una ruta tentativa seleccionada por el usuario")
public class ConfirmarRutaTentativaDTO {

    @Schema(
            description = "ID de la solicitud de traslado asociada",
            example = "102"
    )
    private Long solicitudTrasladoId;

    @Schema(
            description = "Número de la opción de ruta tentativa elegida (1, 2, 3 o 4)",
            example = "2"
    )
    private Integer numeroOpcionSeleccionada;

    @Schema(
            description = "Observaciones adicionales opcionales ingresadas por el usuario",
            example = "Se selecciona la ruta con menor tiempo estimado"
    )
    private String observaciones;
}