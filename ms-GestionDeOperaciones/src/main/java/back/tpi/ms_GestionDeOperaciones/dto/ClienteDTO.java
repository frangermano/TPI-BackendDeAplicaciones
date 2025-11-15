package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa la información básica de un cliente")
public class ClienteDTO {

    @Schema(
            description = "Identificador único del cliente",
            example = "15"
    )
    private Long clienteId;

    @Schema(
            description = "Nombre del cliente",
            example = "Juan"
    )
    private String nombre;

    @Schema(
            description = "Apellido del cliente",
            example = "Pérez"
    )
    private String apellido;

    @Schema(
            description = "Correo electrónico del cliente",
            example = "juan.perez@example.com"
    )
    private String email;

    @Schema(
            description = "Número de teléfono del cliente (solo números)",
            example = "1145638920"
    )
    private int telefono;
}