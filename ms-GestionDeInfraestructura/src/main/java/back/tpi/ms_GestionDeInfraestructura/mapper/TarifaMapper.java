package back.tpi.ms_GestionDeInfraestructura.mapper;

import back.tpi.ms_GestionDeInfraestructura.domain.Tarifa;
import back.tpi.ms_GestionDeInfraestructura.dto.TarifaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TarifaMapper {

    public TarifaDTO toDTO(Tarifa tarifa) {
        if (tarifa == null) {
            log.error("❌ Intento de mapear Tarifa null a TarifaDTO");
            return null;
        }

        log.debug("Mapeando Tarifa ID {} a TarifaDTO", tarifa.getId());
        TarifaDTO dto = TarifaDTO.builder()
                .tarifaId(tarifa.getId())  // ⚠️ CAMPO CORRECTO: tarifaId
                .nombre(tarifa.getNombre())
                .patenteCamion(tarifa.getPatenteCamion())
                .valorCombustibleLitro(tarifa.getValorCombustibleLitro())
                .cargoGestionTrama(tarifa.getCargoGestionTrama())
                .fechaVigencia(tarifa.getFechaVigencia())
                .idTipoCamion(tarifa.getIdTipoCamion())
                .idDeposito(tarifa.getIdDeposito())
                .build();

        log.debug("✅ TarifaDTO mapeado: ID={}, Nombre={}", dto.getTarifaId(), dto.getNombre());
        return dto;
    }
}
