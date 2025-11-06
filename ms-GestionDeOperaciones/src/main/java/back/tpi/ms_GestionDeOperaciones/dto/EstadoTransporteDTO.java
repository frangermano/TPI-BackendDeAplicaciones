package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoTransporteDTO {

    // Identificadores
    private Long solicitudId;
    private Integer numeroSolicitud;
    private Long contenedorId;

    // Estado actual
    private EstadoSolicitud estado;

    // Información del cliente
    private String clienteNombre;
    private String clienteEmail;

    // Información del contenedor
    private Double contenedorPeso;
    private Double contenedorVolumen;

    // Rutas
    private String direccionOrigen;
    private String direccionDestino;

    // Tiempos
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaSolicitud;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaFinalizacion;

    private Double tiempoEstimado;
    private Double tiempoReal;

    // Costos
    private Double costoEstimado;
    private Double costoFinal;

    // Información adicional útil
    private String mensajeEstado;
    private Double progreso; // Porcentaje estimado de progreso (0-100)
}