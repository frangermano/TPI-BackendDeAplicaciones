package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * DTO de respuesta para la consulta de contenedores pendientes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta detallada de la consulta de contenedores pendientes, incluyendo estadísticas y filtros aplicados")
public class ConsultaContenedoresResponseDTO {

    @Schema(
            description = "Lista de contenedores encontrados según los filtros aplicados"
    )
    private List<ContenedorPendienteDTO> contenedores;

    // ---------- Estadísticas generales ----------
    @Schema(description = "Cantidad total de contenedores encontrados", example = "37")
    private Integer totalContenedores;

    @Schema(description = "Cantidad de contenedores actualmente en tránsito", example = "12")
    private Integer contenedoresEnTransito;

    @Schema(description = "Cantidad de contenedores que se encuentran atrasados", example = "5")
    private Integer contenedoresAtrasados;

    @Schema(description = "Cantidad de contenedores ubicados en depósitos", example = "20")
    private Integer contenedoresEnDeposito;

    // ---------- Estadísticas por estado ----------
    @Schema(
            description = "Mapa de estados con el total de contenedores en cada uno",
            example = "{\"EN_PROCESO\": 10, \"ENTREGADO\": 15, \"CANCELADO\": 2}"
    )
    private Map<String, Integer> contenedoresPorEstado;

    // ---------- Estadísticas de peso y volumen ----------
    @Schema(description = "Peso total sumado de todos los contenedores (kg)", example = "15400.5")
    private Double pesoTotal;

    @Schema(description = "Volumen total sumado de todos los contenedores (m3)", example = "320.75")
    private Double volumenTotal;

    @Schema(description = "Peso promedio por contenedor (kg)", example = "416.22")
    private Double pesoPromedio;

    @Schema(description = "Volumen promedio por contenedor (m3)", example = "8.71")
    private Double volumenPromedio;

    // ---------- Estadísticas de costos ----------
    @Schema(description = "Costo estimado total del traslado de todos los contenedores", example = "125000.50")
    private Double costoTotalEstimado;

    @Schema(description = "Costo real acumulado según los tramos ya completados", example = "89350.0")
    private Double costoTotalAcumulado;

    // ---------- Información de paginación ----------
    @Schema(description = "Número de página actual (comienza en 1)", example = "1")
    private Integer paginaActual;

    @Schema(description = "Cantidad de elementos por página", example = "20")
    private Integer tamanoPagina;

    @Schema(description = "Total de páginas disponibles", example = "3")
    private Integer totalPaginas;

    @Schema(description = "Cantidad total de registros disponibles sin paginar", example = "57")
    private Long totalRegistros;

    // ---------- Filtros aplicados ----------
    @Schema(description = "Filtros utilizados en la consulta")
    private FiltrosContenedorDTO filtrosAplicados;

    // ---------- Metadata adicional ----------
    @Schema(description = "Fecha en la que se realizó la consulta", example = "2025-11-13T14:30:00")
    private String fechaConsulta;
}