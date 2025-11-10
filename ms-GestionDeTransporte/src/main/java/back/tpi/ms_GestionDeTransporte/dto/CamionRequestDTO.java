package back.tpi.ms_GestionDeTransporte.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CamionRequestDTO {
    @NotBlank(message = "La patente es obligatoria")
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{3,4}[A-Z]{0,2}$",
            message = "La patente debe tener un formato válido (ej: ABC123)")
    private String patente;

    @NotNull(message = "El costo de combustible es obligatorio")
    @Positive(message = "El costo de combustible debe ser positivo")
    private Double costoCombustible;

    @NotNull(message = "El costo por km es obligatorio")
    @Positive(message = "El costo por km debe ser positivo")
    private Double costoKm;

    private Boolean disponible = true;

    @NotNull(message = "El ID del transportista es obligatorio")
    private Long idTransportista;

    @NotNull(message = "El ID del tipo de camión es obligatorio")
    private Long idTipoCamion;
}
