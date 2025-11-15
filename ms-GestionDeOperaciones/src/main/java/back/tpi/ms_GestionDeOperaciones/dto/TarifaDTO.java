package back.tpi.ms_GestionDeOperaciones.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO que representa una tarifa aplicada al cálculo de costos de transporte o depósito.")
public class TarifaDTO {

    @Schema(
            description = "ID de la tarifa. Puede ser nulo cuando se crea una nueva tarifa.",
            example = "5",
            nullable = true
    )
    private Long tarifaId;

    @Schema(
            description = "Nombre identificatorio de la tarifa.",
            example = "Tarifa Nacional Camión Pesado"
    )
    private String nombre;

    @Schema(
            description = "Patente del camión al que está asociada la tarifa. Puede ser null si es una tarifa general por tipo de camión.",
            example = "AB123CD",
            nullable = true
    )
    private String patenteCamion;

    @Schema(
            description = "Valor del litro de combustible que se toma para el cálculo.",
            example = "980.50"
    )
    private Double valorCombustibleLitro;

    @Schema(
            description = "Cargo fijo administrativo o de gestión por tramo.",
            example = "1500.00"
    )
    private Double cargoGestionTrama;

    @Schema(
            description = "Fecha desde la cual la tarifa entra en vigencia.",
            example = "2025-01-15T00:00:00Z"
    )
    private Date fechaVigencia;

    @Schema(
            description = "Identificador del tipo de camión al que se aplica la tarifa.",
            example = "3"
    )
    private Long idTipoCamion;

    @Schema(
            description = "Identificador del depósito asociado si aplica.",
            example = "12",
            nullable = true
    )
    private Long idDeposito;
}