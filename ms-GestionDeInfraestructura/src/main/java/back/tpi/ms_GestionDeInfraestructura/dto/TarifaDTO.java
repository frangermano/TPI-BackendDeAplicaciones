package back.tpi.ms_GestionDeInfraestructura.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarifaDTO {
    private Long id;

    private String nombre;

    @JsonProperty("patenteCamion")
    private String patenteCamion;

    @JsonProperty("valorCombustibleLitro")
    private Double valorCombustibleLitro;

    @JsonProperty("cargoGestionTrama")
    private Double cargoGestionTrama;

    @JsonProperty("fechaVigencia")
    private Date fechaVigencia;

    @JsonProperty("idTipoCamion")
    private Long idTipoCamion;

    @JsonProperty("idDeposito")
    private Long idDeposito;
}
