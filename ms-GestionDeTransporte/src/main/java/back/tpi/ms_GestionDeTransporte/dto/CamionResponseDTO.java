package back.tpi.ms_GestionDeTransporte.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CamionResponseDTO {
    private String patente;
    private Double costoCombustible;
    private Double costoKm;
    private Boolean disponible;
    private TransportistaResponseDTO transportista;
    private TipoCamionDTO tipoCamion;
}