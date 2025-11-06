package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.Cliente;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
