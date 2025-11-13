package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO simplificado para contenedor pendiente con ubicación y estado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContenedorPendienteDTO {

    // Información básica del contenedor
    private Long contenedorId;
    private Double peso;
    private Double volumen;

    // Información del cliente
    private String clienteNombre;

    // Información de la solicitud
    private Long solicitudId;
    private Integer numeroSolicitud;
    private EstadoSolicitud estado;

    // Ubicación
    private String origen;
    private String destino;
    private String ubicacionActual;

    // Estado y progreso
    private String estadoDetallado;
    private Integer tramosCompletados;
    private Integer totalTramos;
    private Double porcentajeProgreso;

    // Tiempos
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaSolicitud;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaEstimadaEntrega;

    private Integer diasEnTransito;
    private Boolean atrasado;

    // Costos
    private Double costoEstimado;
}
