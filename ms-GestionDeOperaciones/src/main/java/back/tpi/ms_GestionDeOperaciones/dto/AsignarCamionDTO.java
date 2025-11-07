package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

/**
 * DTO para asignar un camión a un tramo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignarCamionDTO {

    private Long tramoId;
    private String patenteCamion;
    private Boolean validarCapacidad; // Si se debe validar capacidad (default: true)
}
