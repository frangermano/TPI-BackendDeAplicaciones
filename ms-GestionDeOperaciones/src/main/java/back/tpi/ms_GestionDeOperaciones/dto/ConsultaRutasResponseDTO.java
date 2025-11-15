package back.tpi.ms_GestionDeOperaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * DTO de respuesta para la consulta de rutas tentativas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta generada al consultar rutas tentativas para una solicitud de traslado")
public class ConsultaRutasResponseDTO {

    // ---------- Información básica de la solicitud ----------

    @Schema(description = "ID de la solicitud de traslado evaluada", example = "120")
    private Long solicitudId;

    @Schema(description = "Dirección de origen del traslado", example = "Av. Corrientes 1234, CABA")
    private String direccionOrigen;

    @Schema(description = "Dirección de destino del traslado", example = "Bv. Oroño 850, Rosario")
    private String direccionDestino;

    @Schema(description = "Peso del contenedor asociado (kg)", example = "450.5")
    private Double pesoContenedor;

    @Schema(description = "Volumen del contenedor asociado (m3)", example = "12.75")
    private Double volumenContenedor;

    // ---------- Rutas sugeridas ----------

    @Schema(
            description = "Listado de rutas tentativas ordenadas según prioridad definida por el sistema"
    )
    private List<RutaTentativaDTO> rutasSugeridas;

    // ---------- Resumen comparativo ----------

    @Schema(
            description = "Ruta con el costo total más bajo entre las opciones sugeridas"
    )
    private RutaTentativaDTO rutaMasEconomica;

    @Schema(
            description = "Ruta con la menor duración estimada"
    )
    private RutaTentativaDTO rutaMasRapida;

    @Schema(
            description = "Ruta recomendada por el sistema considerando un balance entre costo y tiempo"
    )
    private RutaTentativaDTO rutaRecomendada;

    // ---------- Metadatos ----------

    @Schema(
            description = "Cantidad total de rutas tentativas analizadas",
            example = "3"
    )
    private Integer cantidadOpciones;

    @Schema(
            description = "Fecha de generación de la consulta",
            example = "2025-11-13T15:45:10"
    )
    private String fechaConsulta;
}