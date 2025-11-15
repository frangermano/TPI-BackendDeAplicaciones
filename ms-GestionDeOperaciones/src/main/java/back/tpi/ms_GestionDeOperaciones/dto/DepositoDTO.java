package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO que representa un depósito y toda la información relacionada para operaciones y cálculo de rutas")
public class DepositoDTO {

    @Schema(
            description = "Identificador único del depósito",
            example = "10"
    )
    private Long id;

    @Schema(
            description = "Nombre del depósito",
            example = "Depósito Central Córdoba"
    )
    private String nombre;

    @Schema(
            description = "Dirección física del depósito",
            example = "Av. Colón 1452, Córdoba"
    )
    private String direccion;

    @Schema(
            description = "Campo original que almacena coordenadas. Puede ser un valor combinado o no utilizado directamente.",
            example = "null",
            nullable = true
    )
    private Double coordenadas;

    @Schema(
            description = "Costo de estadía o almacenamiento en el depósito",
            example = "1500.0"
    )
    private Double costoEstadia;

    // -------------------------
    // CAMPOS DE COORDENADAS
    // -------------------------

    @Schema(
            description = "Latitud del depósito",
            example = "-31.417339"
    )
    private Double latitud;

    @Schema(
            description = "Longitud del depósito",
            example = "-64.183319"
    )
    private Double longitud;

    // -------------------------
    // CAMPOS CALCULADOS PARA RUTAS
    // -------------------------

    @Schema(
            description = "Distancia estimada desde el origen hasta este depósito (en kilómetros)",
            example = "12.5"
    )
    private Double distanciaDesdeOrigen;

    @Schema(
            description = "Distancia estimada desde este depósito hasta el destino final (en kilómetros)",
            example = "8.3"
    )
    private Double distanciaHastaDestino;

    @Schema(
            description = "Distancia total sumando origen → depósito → destino",
            example = "20.8"
    )
    private Double distanciaTotal;

    @Schema(
            description = "Indica si este depósito se encuentra dentro de la ruta óptima",
            example = "true"
    )
    private Boolean enRuta;

    @Schema(
            description = "Cantidad de kilómetros de desvío respecto de la ruta directa",
            example = "2.1"
    )
    private Double desviacionKm;

    /**
     * Parsea el campo coordenadas (formato esperado: \"lat,lng\" o solo un double).
     * Ajustar este método según el formato real de almacenamiento.
     */
    public void parsearCoordenadas() {
        if (this.coordenadas != null) {
            // Lógica a definir por tu equipo según el formato real.
        }
    }

    /**
     * Constructor estático auxiliar para crear un depósito a partir de coordenadas separadas.
     */
    public static DepositoDTO conCoordenadas(
            Long id,
            String nombre,
            String direccion,
            Double latitud,
            Double longitud,
            Double costoEstadia
    ) {
        DepositoDTO dto = new DepositoDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setDireccion(direccion);
        dto.setLatitud(latitud);
        dto.setLongitud(longitud);
        dto.setCostoEstadia(costoEstadia);
        return dto;
    }
}