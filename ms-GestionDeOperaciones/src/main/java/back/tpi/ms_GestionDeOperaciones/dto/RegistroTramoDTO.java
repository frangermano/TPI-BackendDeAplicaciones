package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

/**
 * DTO para registrar inicio o fin de un tramo por el transportista
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroTramoDTO {

    private Long tramoId;
    private Long transportistaId; // ID del transportista que registra
    private String observaciones; // Observaciones opcionales del transportista

    // Coordenadas GPS opcionales del punto de registro
    private Double latitud;
    private Double longitud;
}