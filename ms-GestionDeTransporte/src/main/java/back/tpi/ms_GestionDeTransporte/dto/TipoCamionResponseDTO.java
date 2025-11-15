package back.tpi.ms_GestionDeTransporte.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta que representa un tipo de camión")
public class TipoCamionResponseDTO {

    @Schema(
            description = "Identificador único del tipo de camión",
            example = "5"
    )
    private Long id;

    @Schema(
            description = "Nombre descriptivo del tipo de camión",
            example = "Camión grúa"
    )
    private String nombre;

    @Schema(
            description = "Capacidad de volumen del camión en metros cúbicos",
            example = "40.0"
    )
    private Double capacidadVolumen;

    @Schema(
            description = "Capacidad máxima de carga en kilogramos",
            example = "15000.0"
    )
    private Double capacidadPeso;
}