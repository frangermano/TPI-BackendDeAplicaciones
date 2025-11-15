package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO que representa un contenedor pendiente de entrega junto con su estado, ubicación y métricas asociadas.")
public class ContenedorPendienteDTO {

    // Información básica del contenedor
    @Schema(description = "Identificador único del contenedor", example = "1201")
    private Long contenedorId;

    @Schema(description = "Peso del contenedor en kilogramos", example = "520.5")
    private Double peso;

    @Schema(description = "Volumen del contenedor en metros cúbicos", example = "12.4")
    private Double volumen;

    // Información del cliente
    @Schema(description = "Nombre del cliente dueño del contenedor", example = "Carlos Gómez")
    private String clienteNombre;

    // Información de la solicitud
    @Schema(description = "Identificador único de la solicitud de traslado", example = "78")
    private Long solicitudId;

    @Schema(description = "Número correlativo de la solicitud", example = "20240115")
    private Integer numeroSolicitud;

    @Schema(description = "Estado actual de la solicitud", example = "EN_TRANSITO")
    private EstadoSolicitud estado;

    // Ubicación
    @Schema(description = "Ciudad de origen del traslado", example = "Buenos Aires")
    private String origen;

    @Schema(description = "Ciudad de destino del traslado", example = "Córdoba")
    private String destino;

    @Schema(description = "Ubicación actual estimada del contenedor", example = "Ruta Nacional 9 - KM 210")
    private String ubicacionActual;

    // Estado y progreso
    @Schema(description = "Descripción detallada del estado actual del contenedor", example = "En tránsito hacia el depósito de Córdoba")
    private String estadoDetallado;

    @Schema(description = "Cantidad de tramos completados hasta el momento", example = "2")
    private Integer tramosCompletados;

    @Schema(description = "Cantidad total de tramos que conforman la ruta", example = "5")
    private Integer totalTramos;

    @Schema(description = "Porcentaje de avance del transporte", example = "40.0")
    private Double porcentajeProgreso;

    // Tiempos
    @Schema(description = "Fecha en la que se solicitó el traslado del contenedor",
            example = "2025-01-22 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaSolicitud;

    @Schema(description = "Fecha estimada de entrega del contenedor",
            example = "2025-01-28 18:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaEstimadaEntrega;

    @Schema(description = "Cantidad de días que el contenedor lleva en tránsito", example = "3")
    private Integer diasEnTransito;

    @Schema(description = "Indica si el contenedor está atrasado respecto a la fecha estimada", example = "true")
    private Boolean atrasado;

    // Costos
    @Schema(description = "Costo total estimado del traslado del contenedor", example = "15400.75")
    private Double costoEstimado;
}