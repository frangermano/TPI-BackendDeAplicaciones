package back.tpi.ms_GestionDeOperaciones.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO que representa un contenedor registrado en el sistema")
public class ContenedorDTO {

    @Schema(
            description = "Identificador único del contenedor",
            example = "501"
    )
    private Long contenedorId;

    @Schema(
            description = "Peso del contenedor en kilogramos",
            example = "480.75"
    )
    private double peso;

    @Schema(
            description = "Volumen del contenedor en metros cúbicos",
            example = "15.2"
    )
    private double volumen;

    @Schema(
            description = "Datos del cliente propietario del contenedor (opcional)",
            implementation = ClienteDTO.class
    )
    private ClienteDTO cliente;
}