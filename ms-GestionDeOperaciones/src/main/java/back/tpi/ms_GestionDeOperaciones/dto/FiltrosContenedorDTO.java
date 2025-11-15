package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO utilizado para aplicar filtros avanzados en búsquedas de contenedores o solicitudes asociadas")
public class FiltrosContenedorDTO {

    @Schema(
            description = "Estado de la solicitud asociada al contenedor",
            example = "EN_CAMINO"
    )
    private EstadoSolicitud estado;

    @Schema(
            description = "Identificador del cliente que generó la solicitud",
            example = "45"
    )
    private Long clienteId;

    // ==========================================
    //            FILTROS POR FECHAS
    // ==========================================

    @Schema(
            description = "Fecha mínima de creación de la solicitud",
            example = "2025-03-10T00:00:00"
    )
    private LocalDateTime fechaSolicitudDesde;

    @Schema(
            description = "Fecha máxima de creación de la solicitud",
            example = "2025-03-20T23:59:59"
    )
    private LocalDateTime fechaSolicitudHasta;

    // ==========================================
    //     FILTROS POR PESO Y VOLUMEN (RANGOS)
    // ==========================================

    @Schema(
            description = "Peso mínimo del contenedor (kg)",
            example = "100.0"
    )
    private Double pesoMinimo;

    @Schema(
            description = "Peso máximo del contenedor (kg)",
            example = "1000.0"
    )
    private Double pesoMaximo;

    @Schema(
            description = "Volumen mínimo del contenedor (m³)",
            example = "2.5"
    )
    private Double volumenMinimo;

    @Schema(
            description = "Volumen máximo del contenedor (m³)",
            example = "20.0"
    )
    private Double volumenMaximo;

    // ==========================================
    //        FILTROS POR UBICACIÓN
    // ==========================================

    @Schema(
            description = "Ciudad de origen del traslado",
            example = "Córdoba"
    )
    private String ciudadOrigen;

    @Schema(
            description = "Ciudad de destino del traslado",
            example = "Rosario"
    )
    private String ciudadDestino;
}