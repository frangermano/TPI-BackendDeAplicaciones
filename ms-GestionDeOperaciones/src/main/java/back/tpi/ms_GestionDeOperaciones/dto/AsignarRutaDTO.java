package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

import java.util.List;

/**
 * DTO para asignar una ruta completa con sus tramos a una solicitud
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignarRutaDTO {

    private Long solicitudTrasladoId;
    private List<TramoDTO> tramos; // Lista de tramos que componen la ruta

    // Opcional: si se quiere calcular automáticamente
    private Boolean calcularAutomaticamente;
}