package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO para representar un tramo tentativo sugerido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "TramoTentativoDTO",
        description = "Representa un tramo sugerido dentro de una ruta tentativa, ya sea de transporte o depósito."
)
public class TramoTentativoDTO {

    @Schema(
            description = "Nombre del punto de origen del tramo",
            example = "Córdoba - Depósito Central"
    )
    private String origen;

    @Schema(
            description = "Nombre del punto de destino del tramo",
            example = "Rosario - Depósito Norte"
    )
    private String destino;

    @Schema(
            description = "Tipo de tramo: TRANSPORTE o DEPOSITO",
            example = "TRANSPORTE"
    )
    private String tipoTramo; // TRANSPORTE, DEPOSITO


    // Coordenadas
    @Schema(
            description = "Latitud del punto de origen",
            example = "-31.4201"
    )
    private Double coordOrigenLat;

    @Schema(
            description = "Longitud del punto de origen",
            example = "-64.1888"
    )
    private Double coordOrigenLng;

    @Schema(
            description = "Latitud del punto de destino",
            example = "-32.9442"
    )
    private Double coordDestinoLat;

    @Schema(
            description = "Longitud del punto de destino",
            example = "-60.6505"
    )
    private Double coordDestinoLng;


    // Estimaciones
    @Schema(
            description = "Distancia del tramo en kilómetros",
            example = "398.7"
    )
    private Double distancia;

    @Schema(
            description = "Costo estimado del tramo",
            example = "52350.75"
    )
    private Double costoEstimado;

    @Schema(
            description = "Tiempo estimado de duración del tramo expresado en horas",
            example = "5h 20m"
    )
    private String tiempoEstimado;
}
