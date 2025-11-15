package back.tpi.ms_GestionDeTransporte.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para crear o actualizar un camión")
public class CamionRequestDTO {

    @Schema(
            description = "Patente del camión en formato válido (ejemplo: ABC123)",
            example = "AB123CD",
            required = true
    )
    @NotBlank(message = "La patente es obligatoria")
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{3,4}[A-Z]{0,2}$",
            message = "La patente debe tener un formato válido (ej: ABC123)")
    private String patente;

    @Schema(
            description = "Costo del combustible por unidad",
            example = "550.75",
            required = true
    )
    @NotNull(message = "El costo de combustible es obligatorio")
    @Positive(message = "El costo de combustible debe ser positivo")
    private Double costoCombustible;

    @Schema(
            description = "Costo por kilómetro recorrido",
            example = "120.50",
            required = true
    )
    @NotNull(message = "El costo por km es obligatorio")
    @Positive(message = "El costo por km debe ser positivo")
    private Double costoKm;

    @Schema(
            description = "Indica si el camión está disponible para asignaciones",
            example = "true"
    )
    private Boolean disponible = true;

    @Schema(
            description = "ID del transportista asignado al camión",
            example = "1",
            required = true
    )
    @NotNull(message = "El ID del transportista es obligatorio")
    private Long idTransportista;

    @Schema(
            description = "ID del tipo de camión",
            example = "2",
            required = true
    )
    @NotNull(message = "El ID del tipo de camión es obligatorio")
    private Long idTipoCamion;
}