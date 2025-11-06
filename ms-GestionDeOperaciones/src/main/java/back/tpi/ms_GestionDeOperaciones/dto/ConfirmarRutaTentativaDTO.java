package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

/**
 * DTO para confirmar y asignar una ruta tentativa seleccionada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmarRutaTentativaDTO {

    private Long solicitudTrasladoId;
    private Integer numeroOpcionSeleccionada; // Número de la ruta tentativa elegida (1, 2, 3, 4)
    private String observaciones; // Observaciones opcionales del usuario
}
