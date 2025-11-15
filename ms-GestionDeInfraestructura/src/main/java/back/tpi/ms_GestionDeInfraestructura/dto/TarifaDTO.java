package back.tpi.ms_GestionDeInfraestructura.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "TarifaDTO",
        description = "Representa una tarifa configurada para un camión. Incluye costos asociados, combustible, gestión y la vigencia aplicable."
)
public class TarifaDTO {

    @Schema(description = "Identificador único de la tarifa", example = "101")
    private Long tarifaId;

    @Schema(description = "Nombre o descripción de la tarifa", example = "Tarifa estándar nacional")
    private String nombre;

    @JsonProperty("patenteCamion")
    @Schema(description = "Patente del camión al que aplica esta tarifa", example = "AC123BD")
    private String patenteCamion;

    @JsonProperty("valorCombustibleLitro")
    @Schema(description = "Costo actual del combustible por litro (AR$)", example = "980.50")
    private Double valorCombustibleLitro;

    @JsonProperty("cargoGestionTrama")
    @Schema(description = "Cargo adicional por gestión administrativa o trámites asociados", example = "1500.00")
    private Double cargoGestionTrama;

    @JsonProperty("fechaVigencia")
    @Schema(
            description = "Fecha desde la cual la tarifa comienza a ser válida",
            example = "2025-01-15T00:00:00Z"
    )
    private Date fechaVigencia;

    @JsonProperty("idTipoCamion")
    @Schema(description = "ID del tipo de camión al que corresponde la tarifa", example = "3")
    private Long idTipoCamion;

    @JsonProperty("idDeposito")
    @Schema(description = "ID del depósito asociado a esta tarifa (si corresponde)", example = "7")
    private Long idDeposito;
}

