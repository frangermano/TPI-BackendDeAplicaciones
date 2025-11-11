package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

/**
 * DTO para representar un tramo tentativo sugerido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TramoTentativoDTO {

    private Integer orden; // Orden del tramo en la ruta (1, 2, 3...)
    private String origen;
    private String destino;
    private String tipoTramo; // TRANSPORTE, DEPOSITO

    // Coordenadas
    private Double coordOrigenLat;
    private Double coordOrigenLng;
    private Double coordDestinoLat;
    private Double coordDestinoLng;

    // Estimaciones para este tramo
    private Double distancia; // en km
    private Double costoEstimado;
    private String tiempoEstimado; // en horas

    // Camión sugerido (opcional)
    private Long camionSugeridoId;
    private String camionSugeridoTipo;

    // Información adicional
    private String descripcion;
    private Boolean requierePermisoEspecial;
}
