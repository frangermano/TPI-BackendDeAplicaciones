package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import back.tpi.ms_GestionDeOperaciones.domain.EstadoTramo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para representar un contenedor pendiente de entrega con su ubicación y estado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenedorPendienteDTO {

    // Información del contenedor
    private Long contenedorId;
    private Double peso;
    private Double volumen;

    // Información del cliente
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;

    // Información de la solicitud
    private Long solicitudId;
    private Integer numeroSolicitud;
    private EstadoSolicitud estadoSolicitud;

    // Ubicación origen y destino
    private String direccionOrigen;
    private String direccionDestino;

    // Ubicación actual
    private String ubicacionActual;
    private Double latitudActual;
    private Double longitudActual;

    // Información de la ruta
    private Long rutaId;
    private Integer tramoActualOrden;
    private String tramoActualOrigen;
    private String tramoActualDestino;
    private EstadoTramo tramoActualEstado;

    // Progreso del viaje
    private Integer totalTramos;
    private Integer tramosCompletados;
    private Double porcentajeCompletado;

    // Tiempos
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaSolicitud;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicioTransporte;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaEstimadaEntrega;

    // Costos
    private Double costoEstimado;
    private Double costoAcumulado;

    // Información adicional
    private Long camionAsignadoId;
    private String estadoDetallado; // Descripción textual del estado actual
    private Integer diasEnTransito;
    private Boolean atrasado;
}
