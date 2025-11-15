package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoTramo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO de respuesta después de registrar inicio/fin de tramo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta que devuelve el estado actualizado de un tramo luego de registrar inicio o fin")
public class RegistroTramoResponseDTO {

    @Schema(
            description = "Identificador del tramo actualizado",
            example = "5012"
    )
    private Long tramoId;

    @Schema(
            description = "Dirección o punto de origen del tramo",
            example = "Depósito Central - Córdoba"
    )
    private String origen;

    @Schema(
            description = "Dirección o punto de destino del tramo",
            example = "Centro Logístico Rosario"
    )
    private String destino;

    @Schema(
            description = "Estado anterior del tramo antes del registro",
            example = "PENDIENTE"
    )
    private EstadoTramo estadoAnterior;

    @Schema(
            description = "Nuevo estado del tramo luego del registro",
            example = "EN_PROGRESO"
    )
    private EstadoTramo estadoNuevo;

    @Schema(
            description = "Fecha y hora de inicio del tramo",
            example = "2025-03-15 14:30:00",
            type = "string"
    )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHoraInicio;

    @Schema(
            description = "Fecha y hora de finalización del tramo",
            example = "2025-03-15 18:45:00",
            type = "string"
    )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHoraFin;

    @Schema(
            description = "Duración del tramo expresada como texto legible (hh:mm)",
            example = "04:15"
    )
    private String duracionHoras;

    @Schema(
            description = "Mensaje de confirmación o información adicional",
            example = "Tramo completado correctamente"
    )
    private String mensaje;

    @Schema(
            description = "Indica si el tramo quedó finalizado",
            example = "true"
    )
    private Boolean tramoCompletado;

    @Schema(
            description = "Costo real calculado para el tramo",
            example = "18750.50"
    )
    private Double costoReal;

    // ----------------------------
    //     INFORMACIÓN DE RUTA
    // ----------------------------

    @Schema(
            description = "Identificador de la ruta a la que pertenece el tramo",
            example = "200"
    )
    private Long rutaId;

    @Schema(
            description = "Cantidad de tramos completados dentro de la ruta",
            example = "2"
    )
    private Integer tramosCompletados;

    @Schema(
            description = "Cantidad total de tramos que componen la ruta",
            example = "4"
    )
    private Integer totalTramos;

    @Schema(
            description = "Indica si todos los tramos de la ruta ya fueron completados",
            example = "false"
    )
    private Boolean todosLosTramosCompletados;
}