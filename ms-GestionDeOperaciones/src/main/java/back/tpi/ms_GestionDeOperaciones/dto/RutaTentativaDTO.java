package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

import java.util.List;

/**
 * DTO para representar una ruta tentativa con tramos sugeridos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaTentativaDTO {

    private Integer numeroOpcion; // Opción 1, 2, 3...
    private String nombreRuta; // Ej: "Ruta Directa", "Ruta con Depósitos"
    private String descripcion; // Descripción de la ruta

    // Tramos sugeridos
    private List<TramoTentativoDTO> tramos;

    // Resumen de la ruta
    private Integer cantidadTramos;
    private Integer cantidadDepositos;
    private Double distanciaTotal; // en km

    // Estimaciones
    private Double costoEstimadoTotal;
    private Double tiempoEstimadoTotal; // en horas

    // Información adicional
    private String tipoRuta; // DIRECTA, ECONOMICA, RAPIDA, BALANCEADA
    private Integer prioridad; // 1 = más recomendada
    private List<String> ventajas;
    private List<String> desventajas;
}
