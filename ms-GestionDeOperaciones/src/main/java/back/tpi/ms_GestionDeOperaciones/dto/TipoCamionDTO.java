package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

/**
 * DTO para representar un tipo de camión
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoCamionDTO {

    private Long id;
    private String nombre;
    private Double capacidadVolumen;
    private Double capacidadPeso;
}