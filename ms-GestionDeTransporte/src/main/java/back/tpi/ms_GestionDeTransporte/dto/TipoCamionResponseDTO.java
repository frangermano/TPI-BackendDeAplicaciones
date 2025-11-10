package back.tpi.ms_GestionDeTransporte.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoCamionResponseDTO {
    private Long id;
    private String nombre;
    private Double capacidadVolumen;
    private Double capacidadPeso;
}