package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoTransporteDTO {

    // ===== CAMPOS OBLIGATORIOS (NUNCA NULL) =====
    private Long solicitudId;
    private EstadoSolicitud estado;
    private String mensajeEstado;

    // Cliente
    private String clienteNombre;
    private String clienteEmail;

    // Contenedor
    private Long contenedorId;
    private Double contenedorPeso;
    private Double contenedorVolumen;

    // Direcciones
    private String direccionOrigen;
    private String direccionDestino;

    // Fecha de solicitud (siempre presente)
    private LocalDateTime fechaSolicitud;

    // ===== CAMPOS OPCIONALES (PUEDEN SER NULL) =====
    // Fechas de proceso
    //private LocalDateTime fechaInicio;
    //private LocalDateTime fechaFinalizacion;

    // Tiempos
   // private Double tiempoEstimado;
   // private Double tiempoReal;

    // Costos
   // private Double costoEstimado;
   // private Double costoFinal;
}