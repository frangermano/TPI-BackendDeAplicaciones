package back.tpi.ms_GestionDeTransporte.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para la creación o actualización de un tipo de camión")
public class TipoCamionRequestDTO {

    @Schema(
            description = "Nombre del tipo de camión",
            example = "Camión térmico",
            required = true
    )
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @Schema(
            description = "Capacidad máxima de volumen del camión en m³ (metros cúbicos)",
            example = "45.0",
            required = true
    )
    @NotNull(message = "La capacidad de volumen es obligatoria")
    @Positive(message = "La capacidad de volumen debe ser positiva")
    private Double capacidadVolumen;

    @Schema(
            description = "Capacidad máxima de peso del camión en kg (kilogramos)",
            example = "12000.0",
            required = true
    )
    @NotNull(message = "La capacidad de peso es obligatoria")
    @Positive(message = "La capacidad de peso debe ser positiva")
    private Double capacidadPeso;
}