package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ruta tentativa con tramos sugeridos y estimaciones de costo/tiempo")
public class RutaTentativaDTO {

    @Schema(
            description = "Número de opción (1, 2, 3...)",
            example = "1",
            minimum = "1"
    )
    private Integer numeroOpcion;

    @Schema(
            description = "Descripción general de la ruta",
            example = "Ruta Directa - Sin depósitos intermedios"
    )
    private String descripcion;

    @Schema(
            description = "Lista de tramos que componen esta ruta (TRANSPORTE o DEPOSITO)",
            example = "[{origen: 'Córdoba', destino: 'Buenos Aires', tipoTramo: 'TRANSPORTE'}]"
    )
    private List<TramoTentativoDTO> tramos;

    @Schema(
            description = "Cantidad total de tramos en la ruta",
            example = "3",
            minimum = "1"
    )
    private Integer cantidadTramos;

    @Schema(
            description = "Cantidad de depósitos intermedios",
            example = "1",
            minimum = "0"
    )
    private Integer cantidadDepositos;

    @Schema(
            description = "Distancia total de la ruta en kilómetros",
            example = "650.50",
            minimum = "0"
    )
    private Double distanciaTotal;

    @Schema(
            description = "Costo total estimado en pesos argentinos",
            example = "45000.50",
            minimum = "0"
    )
    private Double costoEstimadoTotal;

    @Schema(
            description = "Tiempo estimado total en formato legible",
            example = "8:30 hs",
            pattern = "\\d+:\\d{2} hs"
    )
    private String tiempoEstimadoTotal;

    @Schema(
            description = "Ventajas de esta ruta",
            example = "[\"Menor tiempo de entrega\", \"Ruta más simple\"]"
    )
    private List<String> ventajas;

    @Schema(
            description = "Desventajas de esta ruta",
            example = "[\"Sin puntos de descanso\", \"Mayor riesgo en caso de problemas\"]"
    )
    private List<String> desventajas;
}