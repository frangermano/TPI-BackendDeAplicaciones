package back.tpi.ms_GestionDeTransporte.mapper;

import back.tpi.ms_GestionDeTransporte.domain.Transportista;
import back.tpi.ms_GestionDeTransporte.dto.TransportistaRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.TransportistaResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TransportistaMapper {

    public Transportista toEntity(TransportistaRequestDTO dto) {
        return Transportista.builder()
                .nombre(dto.getNombre())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .disponible(dto.getDisponible() != null ? dto.getDisponible() : true)
                .build();
    }

    public TransportistaResponseDTO toResponseDTO(Transportista entity) {
        return TransportistaResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .telefono(entity.getTelefono())
                .email(entity.getEmail())
                .disponible(entity.getDisponible())
                .build();
    }
}
