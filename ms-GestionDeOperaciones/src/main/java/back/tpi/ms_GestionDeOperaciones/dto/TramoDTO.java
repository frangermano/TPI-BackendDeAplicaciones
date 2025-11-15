package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoTramo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Información detallada de un tramo de una ruta logística.")
public class TramoDTO {

    @Schema(
            description = "ID único del tramo.",
            example = "12045"
    )
    private Long tramoId;

    @Schema(
            description = "Ciudad o punto de partida del tramo.",
            example = "Buenos Aires"
    )
    private String origen;

    @Schema(
            description = "Ciudad o punto de destino del tramo.",
            example = "Rosario"
    )
    private String destino;

    @Schema(
            description = "Tipo de tramo: transporte o depósito.",
            allowableValues = {"TRANSPORTE", "DEPOSITO"},
            example = "TRANSPORTE"
    )
    private String tipoTramo;

    @Schema(
            description = "Estado actual del tramo.",
            example = "EN_PROGRESO"
    )
    private EstadoTramo estado;

    @Schema(
            description = "Costo estimado del tramo antes de ejecutarse.",
            example = "15000.0"
    )
    private Double costoAproximado;

    @Schema(
            description = "Costo real calculado tras la ejecución del tramo.",
            example = "15250.5"
    )
    private Double costoReal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Fecha y hora de inicio del tramo.",
            example = "2025-02-14 08:30:00"
    )
    private LocalDateTime fechaHoraInicio;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Fecha y hora de finalización del tramo.",
            example = "2025-02-14 14:50:00"
    )
    private LocalDateTime fechaHoraFin;

    @Schema(
            description = "Patente del camión asignado en caso de tramos de transporte.",
            example = "AB123CD"
    )
    private String camionPatente;

    // ============================= COORDENADAS =============================

    @Schema(description = "Latitud del punto de origen.", example = "-34.603722")
    private Double coordOrigenLat;

    @Schema(description = "Longitud del punto de origen.", example = "-58.381592")
    private Double coordOrigenLng;

    @Schema(description = "Latitud del punto de destino.", example = "-32.946819")
    private Double coordDestinoLat;

    @Schema(description = "Longitud del punto de destino.", example = "-60.639316")
    private Double coordDestinoLng;

    // ============================= DISTANCIA / DURACIÓN =============================

    @Schema(
            description = "Distancia total del tramo en kilómetros.",
            example = "302.5"
    )
    private Double distancia;

    @Schema(
            description = "Tiempo estimado del tramo en formato legible.",
            example = "4h 25m"
    )
    private String tiempoEstimado;
}