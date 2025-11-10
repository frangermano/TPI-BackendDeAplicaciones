package back.tpi.ms_GestionDeTransporte.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportistaResponseDTO {
    private Long id;
    private String nombre;
    private String telefono;
    private String email;
    private Boolean disponible;
}
