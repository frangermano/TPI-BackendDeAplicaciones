package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.Cliente;
import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolicitudTrasladoDTO {

    private Long id;

    // Datos del Cliente (se registra si no existe)
    private ClienteDTO cliente;

    // Datos del Contenedor (se crea nuevo)
    private ContenedorDTO contenedor;

    // Datos de la Tarifa (se crea nueva)
    private TarifaDTO tarifa;

    // Datos del traslado
    private String direccionOrigen;
    private Double coordOrigenLat;
    private Double coordOrigenLng;

    private String direccionDestino;
    private Double coordDestinoLat;
    private Double coordDestinoLng;

    private RutaDTO ruta;

    private EstadoSolicitud estado;
    private Double costoEstimado;
    private String tiempoEstimado;
    private LocalDateTime fechaSolicitud;
}
