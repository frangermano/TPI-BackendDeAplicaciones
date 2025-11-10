package back.tpi.ms_GestionDeTransporte.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoCamionRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @NotNull(message = "La capacidad de volumen es obligatoria")
    @Positive(message = "La capacidad de volumen debe ser positiva")
    private Double capacidadVolumen;

    @NotNull(message = "La capacidad de peso es obligatoria")
    @Positive(message = "La capacidad de peso debe ser positiva")
    private Double capacidadPeso;
}
