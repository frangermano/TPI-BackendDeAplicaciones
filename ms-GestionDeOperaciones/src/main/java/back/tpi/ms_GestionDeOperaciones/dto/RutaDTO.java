package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaDTO {

    private Long id;
    private Long solicitudTrasladoId;
    private Integer cantidadTramos;
    private Integer cantidadDepositos;
    private List<TramoDTO> tramos;
}
