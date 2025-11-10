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

    @JsonProperty("patente_camion")
    private String patenteCamion;

    @JsonProperty("valor_combustible_litro")
    private double valorCombustibleLitro;

    @JsonProperty("cargo_gestion_trama")
    private double cargoGestionTrama;

    @JsonProperty("fecha_vigencia")
    private Date fechaVigencia;

    @JsonProperty("id_tipo_camion")
    private Long idTipoCamion;

    @JsonProperty("id_deposito")
    private Long idDeposito;
}
