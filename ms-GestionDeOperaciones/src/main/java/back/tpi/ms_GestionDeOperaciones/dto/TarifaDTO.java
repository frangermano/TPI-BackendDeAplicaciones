package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarifaDTO {
    private Long id; // Puede venir null si es nueva
    private String nombre;
    private String patenteCamion;
    private double valorCombustibleLitro;
    private double cargoGestionTrama;
    private Date fechaVigencia;
    private Long idTipoCamion;
    private Long idDeposito;
}
