package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

import java.util.List;

/**
 * DTO de respuesta para la consulta de rutas tentativas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaRutasResponseDTO {

    // Información de la solicitud
    private Long solicitudId;
    private String direccionOrigen;
    private String direccionDestino;
    private Double pesoContenedor;
    private Double volumenContenedor;

    // Rutas sugeridas (ordenadas por prioridad)
    private List<RutaTentativaDTO> rutasSugeridas;

    // Resumen comparativo
    private RutaTentativaDTO rutaMasEconomica;
    private RutaTentativaDTO rutaMasRapida;
    private RutaTentativaDTO rutaRecomendada;

    // Metadatos
    private Integer cantidadOpciones;
    private String fechaConsulta;
}