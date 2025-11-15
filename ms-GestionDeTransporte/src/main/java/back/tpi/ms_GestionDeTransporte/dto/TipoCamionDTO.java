package back.tpi.ms_GestionDeTransporte.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa un tipo de camión con sus capacidades")
public class TipoCamionDTO {

    @Schema(
            description = "Identificador único del tipo de camión",
            example = "3"
    )
    private Long id;

    @Schema(
            description = "Nombre o descripción del tipo de camión",
            example = "Camión refrigerado"
    )
    private String nombre;

    @Schema(
            description = "Capacidad máxima de volumen del camión en metros cúbicos",
            example = "45.0"
    )
    private Double capacidadVolumen;

    @Schema(
            description = "Capacidad máxima de peso del camión en kilogramos",
            example = "12000.0"
    )
    private Double capacidadPeso;
}