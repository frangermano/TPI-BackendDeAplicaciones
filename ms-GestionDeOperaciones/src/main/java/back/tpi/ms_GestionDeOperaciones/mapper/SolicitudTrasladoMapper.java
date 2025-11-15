package back.tpi.ms_GestionDeOperaciones.mapper;

import back.tpi.ms_GestionDeOperaciones.client.TarifaClient;
import back.tpi.ms_GestionDeOperaciones.domain.SolicitudTraslado;
import back.tpi.ms_GestionDeOperaciones.dto.SolicitudTrasladoDTO;
import back.tpi.ms_GestionDeOperaciones.dto.TarifaDTO;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SolicitudTrasladoMapper {

    private final ClienteMapper clienteMapper;
    private final ContenedorMapper contenedorMapper;
    private final TarifaClient tarifaClient;
    private final RutaMapper rutaMapper;

    public SolicitudTrasladoDTO toDTO(SolicitudTraslado solicitudTraslado) {
        return SolicitudTrasladoDTO.builder()
                .solicitudId(solicitudTraslado.getId())
                .cliente(clienteMapper.toDTO(solicitudTraslado.getCliente()))
                .contenedor(contenedorMapper.toDTO(solicitudTraslado.getContenedor()))
                .tarifa(tarifaClient.getTarifa(solicitudTraslado.getTarifaId()))
                .direccionOrigen(solicitudTraslado.getDireccionOrigen())
                .coordOrigenLat(solicitudTraslado.getCoordOrigenLat())
                .coordOrigenLng(solicitudTraslado.getCoordOrigenLng())
                .direccionDestino(solicitudTraslado.getDireccionDestino())
                .coordDestinoLat(solicitudTraslado.getCoordDestinoLat())
                .coordDestinoLng(solicitudTraslado.getCoordDestinoLng())
                .distanciaLegible(solicitudTraslado.getDistanciaLegible())
                .estado(solicitudTraslado.getEstado())
                .costoEstimado(solicitudTraslado.getCostoEstimado())
                .tiempoEstimado(solicitudTraslado.getTiempoEstimado())
                .fechaSolicitud(solicitudTraslado.getFechaSolicitud())
                .ruta(
                        solicitudTraslado.getRuta() != null
                                ? rutaMapper.toDTO(solicitudTraslado.getRuta())
                                : null
                )
                .build();
    }


    public SolicitudTrasladoDTO toDTOSolictudCreada(SolicitudTraslado solicitudTraslado, TarifaDTO tarifaDTO){
        return SolicitudTrasladoDTO.builder()
                .solicitudId(solicitudTraslado.getId())
                .cliente(clienteMapper.toDTO(solicitudTraslado.getCliente()))
                .contenedor(contenedorMapper.toDTO(solicitudTraslado.getContenedor()))
                .tarifa(tarifaDTO)
                .direccionOrigen(solicitudTraslado.getDireccionOrigen())
                .coordOrigenLat(solicitudTraslado.getCoordOrigenLat())
                .coordOrigenLng(solicitudTraslado.getCoordOrigenLng())
                .direccionDestino(solicitudTraslado.getDireccionDestino())
                .coordDestinoLat(solicitudTraslado.getCoordDestinoLat())
                .coordDestinoLng(solicitudTraslado.getCoordDestinoLng())
                .distanciaLegible(solicitudTraslado.getDistanciaLegible())
                .estado(solicitudTraslado.getEstado())
                .costoEstimado(solicitudTraslado.getCostoEstimado())
                .tiempoEstimado(solicitudTraslado.getTiempoEstimado())
                .fechaSolicitud(solicitudTraslado.getFechaSolicitud())
                .build();
    }

}
