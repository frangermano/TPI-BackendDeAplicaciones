package back.tpi.ms_GestionDeTransporte.mapper;

import back.tpi.ms_GestionDeTransporte.domain.Camion;
import back.tpi.ms_GestionDeTransporte.dto.CamionResponseDTO;
import back.tpi.ms_GestionDeTransporte.dto.TipoCamionDTO;
import org.springframework.stereotype.Component;

@Component
public class CamionMapper {

    private final TransportistaMapper transportistaMapper;

    public CamionMapper(TransportistaMapper transportistaMapper) {
        this.transportistaMapper = transportistaMapper;
    }

    public CamionResponseDTO toResponseDTO(Camion entity) {
        return CamionResponseDTO.builder()
                .patente(entity.getPatente())
                .costoCombustible(entity.getCostoCombustible())
                .costoKm(entity.getCostoKm())
                .disponible(entity.getDisponible())
                .transportista(transportistaMapper.toResponseDTO(entity.getTransportista()))
                .tipoCamion(toTipoCamionDTO(entity.getTipoCamion()))
                .build();
    }

    private TipoCamionDTO toTipoCamionDTO(back.tpi.ms_GestionDeTransporte.domain.TipoCamion entity) {
        return TipoCamionDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .capacidadVolumen(entity.getCapacidadVolumen())
                .capacidadPeso(entity.getCapacidadPeso())
                .build();
    }
}