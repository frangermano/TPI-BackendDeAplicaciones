package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltrosContenedorDTO {

    private EstadoSolicitud estado;
    private Long clienteId;


    // Por rango de fechas de solicitud
    private LocalDateTime fechaSolicitudDesde;
    private LocalDateTime fechaSolicitudHasta;

    // Por rangos de peso/volumen (útil para logística)
    private Double pesoMinimo;
    private Double pesoMaximo;
    private Double volumenMinimo;
    private Double volumenMaximo;

    // Por ubicación (origen o destino)
    private String ciudadOrigen;
    private String ciudadDestino;
}