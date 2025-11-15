package back.tpi.ms_GestionDeInfraestructura.mapper;

import back.tpi.ms_GestionDeInfraestructura.domain.Tarifa;
import back.tpi.ms_GestionDeInfraestructura.dto.TarifaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TarifaMapper {

    public TarifaDTO toDTO(Tarifa tarifa) {
        return TarifaDTO.builder()
                .id(tarifa.getId())
                .nombre(tarifa.getNombre())
                .patenteCamion(tarifa.getPatenteCamion())
                .valorCombustibleLitro(tarifa.getValorCombustibleLitro())
                .cargoGestionTrama(tarifa.getCargoGestionTrama())
                .fechaVigencia(tarifa.getFechaVigencia())
                .idTipoCamion(tarifa.getIdTipoCamion())
                .idDeposito(tarifa.getIdDeposito())
                .build();
    }
}
