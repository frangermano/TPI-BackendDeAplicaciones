package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para registrar inicio o fin de un tramo por parte del transportista
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO utilizado por el transportista para registrar el inicio o fin de un tramo de transporte")
public class RegistroTramoDTO {

    @Schema(
            description = "Identificador del tramo que se está registrando",
            example = "3001",
            required = true
    )
    private Long tramoId;

    @Schema(
            description = "ID del transportista que realiza el registro",
            example = "12",
            required = true
    )
    private Long transportistaId;

    @Schema(
            description = "Observaciones opcionales agregadas por el transportista",
            example = "Carga revisada, sin novedades"
    )
    private String observaciones;

    // ---------- Coordenadas opcionales ----------

    @Schema(
            description = "Latitud del punto donde se realiza el registro",
            example = "-31.417339",
            nullable = true
    )
    private Double latitud;

    @Schema(
            description = "Longitud del punto donde se realiza el registro",
            example = "-64.183319",
            nullable = true
    )
    private Double longitud;
}