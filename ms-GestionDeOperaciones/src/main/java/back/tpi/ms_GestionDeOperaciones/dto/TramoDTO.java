package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoTramo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TramoDTO {

    private Long id;
    private String origen;
    private String destino;
    private String tipoTramo; // TRANSPORTE, DEPOSITO
    private EstadoTramo estado;
    private Double costoAproximado;
    private Double costoReal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHoraInicio;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHoraFin;

    private String camionPatente;

    // Coordenadas opcionales
    private Double coordOrigenLat;
    private Double coordOrigenLng;
    private Double coordDestinoLat;
    private Double coordDestinoLng;

    private Double distancia;
    private String tiempoEstimado;
}