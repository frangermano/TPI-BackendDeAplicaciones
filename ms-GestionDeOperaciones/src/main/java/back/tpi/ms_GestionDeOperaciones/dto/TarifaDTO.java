package back.tpi.ms_GestionDeOperaciones.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TarifaDTO {
    private Long id; // Puede venir null si es nueva
    private String nombre;
    private String patenteCamion;
    private Double valorCombustibleLitro;
    private Double cargoGestionTrama;
    private Date fechaVigencia;
    private Long idTipoCamion;
    private Long idDeposito;

}

