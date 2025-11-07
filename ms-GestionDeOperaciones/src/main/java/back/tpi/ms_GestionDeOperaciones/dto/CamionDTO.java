package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

/**
 * DTO para representar un camión del microservicio de transporte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CamionDTO {

    private String patente;
    private Double costoCombustible;
    private Double costoKm;
    private Boolean disponible;
    private Long transportistaId;
    private TipoCamionDTO tipoCamion;
}
