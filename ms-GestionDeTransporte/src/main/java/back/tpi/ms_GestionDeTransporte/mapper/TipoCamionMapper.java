package back.tpi.ms_GestionDeTransporte.mapper;

import back.tpi.ms_GestionDeTransporte.domain.TipoCamion;
import back.tpi.ms_GestionDeTransporte.dto.TipoCamionRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.TipoCamionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TipoCamionMapper {

    public TipoCamion toEntity(TipoCamionRequestDTO dto) {
        return TipoCamion.builder()
                .nombre(dto.getNombre())
                .capacidadVolumen(dto.getCapacidadVolumen())
                .capacidadPeso(dto.getCapacidadPeso())
                .build();
    }

    public TipoCamionResponseDTO toResponseDTO(TipoCamion entity) {
        return TipoCamionResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .capacidadVolumen(entity.getCapacidadVolumen())
                .capacidadPeso(entity.getCapacidadPeso())
                .build();
    }
}