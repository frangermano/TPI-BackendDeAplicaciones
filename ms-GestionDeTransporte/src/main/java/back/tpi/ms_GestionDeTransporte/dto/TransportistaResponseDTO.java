package back.tpi.ms_GestionDeTransporte.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta que representa la información de un transportista")
public class TransportistaResponseDTO {

    @Schema(
            description = "Identificador único del transportista",
            example = "12"
    )
    private Long id;

    @Schema(
            description = "Nombre completo del transportista",
            example = "Juan Pérez"
    )
    private String nombre;

    @Schema(
            description = "Número de teléfono del transportista",
            example = "+54 351 5678901"
    )
    private String telefono;

    @Schema(
            description = "Correo electrónico del transportista",
            example = "juan.perez@example.com"
    )
    private String email;

    @Schema(
            description = "Disponibilidad del transportista",
            example = "true"
    )
    private Boolean disponible;
}