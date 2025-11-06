package back.tpi.ms_GestionDeOperaciones.dto;

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
public class ConsultaContenedoresResponseDTO {

    // Contenedores encontrados
    private List<ContenedorPendienteDTO> contenedores;

    // Estadísticas generales
    private Integer totalContenedores;
    private Integer contenedoresEnTransito;
    private Integer contenedoresAtrasados;
    private Integer contenedoresEnDeposito;

    // Estadísticas por estado
    private Map<String, Integer> contenedoresPorEstado;

    // Estadísticas de peso y volumen
    private Double pesoTotal;
    private Double volumenTotal;
    private Double pesoPromedio;
    private Double volumenPromedio;

    // Estadísticas de costos
    private Double costoTotalEstimado;
    private Double costoTotalAcumulado;

    // Información de paginación
    private Integer paginaActual;
    private Integer tamanoPagina;
    private Integer totalPaginas;
    private Long totalRegistros;

    // Filtros aplicados
    private FiltrosContenedorDTO filtrosAplicados;

    // Metadata
    private String fechaConsulta;
}
