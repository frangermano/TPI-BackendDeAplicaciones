package back.tpi.ms_GestionDeOperaciones.mapper;

import back.tpi.ms_GestionDeOperaciones.client.OsrmClient;
import back.tpi.ms_GestionDeOperaciones.domain.Tramo;
import back.tpi.ms_GestionDeOperaciones.dto.DistanciaResponse;
import back.tpi.ms_GestionDeOperaciones.dto.TramoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TramoMapper {

    private final OsrmClient osrmClient;

    public TramoDTO toDTO(Tramo tramo) {
        Double distancia = tramo.getDistancia();
        String tiempoEstimado;

        // Si la distancia no está calculada o es 0, consultamos a OSRM
        if (distancia == null || distancia == 0.0) {
            try {
                DistanciaResponse resp = osrmClient.calcularDistancia(
                        tramo.getCoordOrigenLat(),
                        tramo.getCoordOrigenLng(),
                        tramo.getCoordDestinoLat(),
                        tramo.getCoordDestinoLng()
                );
                distancia = resp.getDistanciaKm();
                tiempoEstimado = resp.getTiempoLegible();
            } catch (Exception e) {
                log.warn("⚠️ No se pudo calcular distancia para tramo ID {}: {}", tramo.getId(), e.getMessage());
                distancia = 0.0;
                tiempoEstimado = "Desconocido";
            }
        } else {
            double velocidadPromedio = 80.0;
            double horas = distancia / velocidadPromedio;
            tiempoEstimado = String.format("%.1f h", horas);
        }

        return TramoDTO.builder()
                .id(tramo.getId())
                .origen(tramo.getOrigen())
                .destino(tramo.getDestino())
                .tipoTramo(tramo.getTipoTramo())
                .estado(tramo.getEstado())
                .costoAproximado(tramo.getCostoAproximado())
                .costoReal(tramo.getCostoReal())
                .fechaHoraInicio(tramo.getFechaHoraInicio())
                .fechaHoraFin(tramo.getFechaHoraFin())
                .camionPatente(tramo.getCamionPatente())
                .coordOrigenLat(tramo.getCoordOrigenLat())
                .coordOrigenLng(tramo.getCoordOrigenLng())
                .coordDestinoLat(tramo.getCoordDestinoLat())
                .coordDestinoLng(tramo.getCoordDestinoLng())
                .distancia(distancia)
                .tiempoEstimado(tiempoEstimado)
                .build();
    }

}
