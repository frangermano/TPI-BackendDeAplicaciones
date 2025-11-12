package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoTramo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta después de registrar inicio/fin de tramo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroTramoResponseDTO {

    private Long tramoId;
    private String origen;
    private String destino;
    private EstadoTramo estadoAnterior;
    private EstadoTramo estadoNuevo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHoraInicio;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHoraFin;

    private String duracionHoras; // Duración calculada del tramo
    private String mensaje;
    private Boolean tramoCompletado;
    private Double costoReal;

    // Información de la ruta
    private Long rutaId;
    private Integer tramosCompletados;
    private Integer totalTramos;
    private Boolean todosLosTramosCompletados;
}
