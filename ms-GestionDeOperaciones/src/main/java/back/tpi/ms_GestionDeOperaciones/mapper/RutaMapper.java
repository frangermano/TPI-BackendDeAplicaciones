package back.tpi.ms_GestionDeOperaciones.mapper;

import back.tpi.ms_GestionDeOperaciones.domain.Ruta;
import back.tpi.ms_GestionDeOperaciones.dto.RutaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class RutaMapper {

    private final TramoMapper tramoMapper;

    public RutaDTO toDTO(Ruta ruta) {
        if (ruta == null) {
            return null;
        }

        return RutaDTO.builder()
                .id(ruta.getId())
                .solicitudTrasladoId(ruta.getSolicitudTraslado() != null ? ruta.getSolicitudTraslado().getId() : null)
                .cantidadTramos(ruta.getCantidadTramos())
                .cantidadDepositos(ruta.getCantidadDepositos())
                .tramos(
                        ruta.getTramos() != null
                                ? ruta.getTramos().stream()
                                .map(tramoMapper::toDTO)
                                .collect(Collectors.toList())
                                : null
                )
                .build();
    }

}
