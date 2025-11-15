package back.tpi.ms_GestionDeTransporte.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para la creación o actualización de un transportista")
public class TransportistaRequestDTO {

    @Schema(
            description = "Nombre completo del transportista",
            example = "Juan Pérez",
            required = true
    )
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Schema(
            description = "Número de teléfono del transportista",
            example = "+54 351 1234567",
            required = true
    )
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Schema(
            description = "Correo electrónico del transportista",
            example = "juan.perez@example.com",
            required = true
    )
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @Schema(
            description = "Indica si el transportista está disponible",
            example = "true"
    )
    private Boolean disponible = true;
}