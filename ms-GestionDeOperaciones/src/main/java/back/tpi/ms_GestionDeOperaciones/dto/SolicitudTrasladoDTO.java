package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "SolicitudTrasladoDTO",
        description = "Representa una solicitud de traslado generada por un cliente, incluyendo datos del contenedor, tarifa, ubicación y estado."
)
public class SolicitudTrasladoDTO {

    @Schema(
            description = "ID único de la solicitud de traslado",
            example = "101"
    )
    private Long solicitudId;

    @Schema(
            description = "Datos del cliente asociado a la solicitud"
    )
    private ClienteDTO cliente;

    @Schema(
            description = "Datos del contenedor solicitado"
    )
    private ContenedorDTO contenedor;

    @Schema(
            description = "Datos de la tarifa aplicada al traslado"
    )
    private TarifaDTO tarifa;


    // ORIGEN
    @Schema(
            description = "Dirección de origen donde se debe retirar el contenedor",
            example = "Av. Colón 1234, Córdoba"
    )
    private String direccionOrigen;

    @Schema(
            description = "Latitud del punto de origen",
            example = "-31.4120"
    )
    private Double coordOrigenLat;

    @Schema(
            description = "Longitud del punto de origen",
            example = "-64.1888"
    )
    private Double coordOrigenLng;


    // DESTINO
    @Schema(
            description = "Dirección de destino donde debe entregarse el contenedor",
            example = "Bv. Oroño 850, Rosario"
    )
    private String direccionDestino;

    @Schema(
            description = "Latitud del punto de destino",
            example = "-32.9442"
    )
    private Double coordDestinoLat;

    @Schema(
            description = "Longitud del punto de destino",
            example = "-60.6505"
    )
    private Double coordDestinoLng;


    @Schema(
            description = "Ruta confirmada asociada a esta solicitud (puede ser null si aún no fue confirmada)"
    )
    private RutaDTO ruta;

    @Schema(
            description = "Estado actual de la solicitud",
            example = "APROBADA",
            implementation = EstadoSolicitud.class
    )
    private EstadoSolicitud estado;

    @Schema(
            description = "Costo estimado total del traslado",
            example = "152300.50"
    )
    private Double costoEstimado;

    @Schema(
            description = "Duración estimada del traslado expresada en formato legible",
            example = "8h 45m"
    )
    private String tiempoEstimado;

    @Schema(
            description = "Fecha y hora en que el cliente generó la solicitud",
            example = "2025-05-14T10:32:00"
    )
    private LocalDateTime fechaSolicitud;

    @Schema(
            description = "Distancia total del traslado en formato legible",
            example = "412 km"
    )
    private String distanciaLegible;
}
