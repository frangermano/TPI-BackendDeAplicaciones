package back.tpi.ms_GestionDeTransporte.dto;

import lombok.*;

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