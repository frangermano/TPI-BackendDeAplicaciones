package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import back.tpi.ms_GestionDeOperaciones.domain.EstadoTramo;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para filtrar la consulta de contenedores pendientes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltrosContenedorDTO {

    // Filtro por cliente
    private Long clienteId;
    private String clienteNombre;

    // Filtro por estado de solicitud
    private EstadoSolicitud estadoSolicitud;

    // Filtro por estado de tramo actual
    private EstadoTramo estadoTramo;

    // Filtro por rango de peso
    private Double pesoMinimo;
    private Double pesoMaximo;

    // Filtro por rango de volumen
    private Double volumenMinimo;
    private Double volumenMaximo;

    // Filtro por fechas
    private LocalDateTime fechaSolicitudDesde;
    private LocalDateTime fechaSolicitudHasta;

    // Filtro por ubicación
    private String ciudadOrigen;
    private String ciudadDestino;

    // Filtro por progreso
    private Double porcentajeCompletadoMinimo;
    private Double porcentajeCompletadoMaximo;

    // Filtro por costo
    private Double costoMinimo;
    private Double costoMaximo;

    // Filtro por atrasos
    private Boolean soloAtrasados;

    // Filtro por camión
    private Long camionId;

    // Ordenamiento
    private String ordenarPor; // fecha, peso, volumen, progreso, costo
    private String direccionOrden; // ASC, DESC

    // Paginación
    private Integer pagina;
    private Integer tamanoPagina;
}
