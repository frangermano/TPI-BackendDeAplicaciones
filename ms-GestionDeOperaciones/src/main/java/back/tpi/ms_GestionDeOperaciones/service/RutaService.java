package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.client.OsrmClient;
import back.tpi.ms_GestionDeOperaciones.domain.*;
import back.tpi.ms_GestionDeOperaciones.dto.AsignarRutaDTO;
import back.tpi.ms_GestionDeOperaciones.dto.DistanciaResponse;
import back.tpi.ms_GestionDeOperaciones.dto.RutaDTO;
import back.tpi.ms_GestionDeOperaciones.dto.TramoDTO;
import back.tpi.ms_GestionDeOperaciones.mapper.RutaMapper;
import back.tpi.ms_GestionDeOperaciones.mapper.TramoMapper;
import back.tpi.ms_GestionDeOperaciones.repository.RutaRepository;
import back.tpi.ms_GestionDeOperaciones.repository.SolicitudTrasladoRepository;
import back.tpi.ms_GestionDeOperaciones.repository.TramoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RutaService {

    private final RutaRepository rutaRepository;
    private final TramoRepository tramoRepository;
    private final SolicitudTrasladoRepository solicitudRepository;
    private final OsrmClient osrmClient;
    private final RutaMapper rutaMapper;
    private final TramoMapper tramoMapper;

    /**
     * Asigna una ruta completa con todos sus tramos a una solicitud de traslado
     */
    @Transactional
    public RutaDTO asignarRutaConTramos(AsignarRutaDTO asignarRutaDTO) {
        log.info("Iniciando asignación de ruta a solicitud ID: {}", asignarRutaDTO.getSolicitudTrasladoId());

        // 1. Verificar que la solicitud existe
        SolicitudTraslado solicitud = solicitudRepository.findById(asignarRutaDTO.getSolicitudTrasladoId())
                .orElseThrow(() -> new RuntimeException(
                        "Solicitud no encontrada con ID: " + asignarRutaDTO.getSolicitudTrasladoId()));

        // 2. Verificar que la solicitud esté en estado APROBADA
        if (solicitud.getEstado() != EstadoSolicitud.APROBADA) {
            throw new RuntimeException(
                    "La solicitud debe estar en estado APROBADA para asignar ruta. Estado actual: " + solicitud.getEstado());
        }

        // 3. Verificar si ya tiene una ruta asignada
        if (rutaRepository.existsBySolicitudTrasladoId(solicitud.getId())) {
            throw new RuntimeException("La solicitud ya tiene una ruta asignada");
        }

        // 4. Validar que hay tramos
        if (asignarRutaDTO.getTramos() == null || asignarRutaDTO.getTramos().isEmpty()) {
            throw new RuntimeException("Debe proporcionar al menos un tramo para la ruta");
        }

        // 5. Crear la ruta
        Ruta ruta = Ruta.builder()
                .solicitudTraslado(solicitud)
                .cantidadTramos(0)
                .cantidadDepositos(0)
                .tramos(new ArrayList<>())
                .build();

        // 6. Crear y agregar todos los tramos
        for (TramoDTO tramoDTO : asignarRutaDTO.getTramos()) {
            Tramo tramo = Tramo.builder()
                    .origen(tramoDTO.getOrigen())
                    .destino(tramoDTO.getDestino())
                    .tipoTramo(tramoDTO.getTipoTramo())
                    .estado(EstadoTramo.PENDIENTE) // Todos los tramos inician como PENDIENTE
                    .costoAproximado(tramoDTO.getCostoAproximado())
                    .costoReal(null)
                    .fechaHoraInicio(null)
                    .fechaHoraFin(null)
                    .camionPatente(tramoDTO.getCamionPatente())
                    .coordOrigenLat(tramoDTO.getCoordOrigenLat())
                    .coordOrigenLng(tramoDTO.getCoordOrigenLng())
                    .coordDestinoLat(tramoDTO.getCoordDestinoLat())
                    .coordDestinoLng(tramoDTO.getCoordDestinoLng())
                    .ruta(ruta)
                    .build();

            ruta.agregarTramo(tramo);
        }

        // 7. Calcular cantidad de depósitos
        ruta.calcularCantidadDepositos();

        // 8. Guardar la ruta (cascade guardará los tramos)
        Ruta rutaGuardada = rutaRepository.save(ruta);

        log.info("Ruta asignada exitosamente con ID: {}, {} tramos, {} depósitos",
                rutaGuardada.getId(),
                rutaGuardada.getCantidadTramos(),
                rutaGuardada.getCantidadDepositos());

        // 9. Retornar DTO
        return rutaMapper.toDTO(rutaGuardada);
    }

    /**
     * Obtiene la ruta asignada a una solicitud
     */
    @Transactional(readOnly = true)
    public RutaDTO obtenerRutaPorSolicitud(Long solicitudId) {
        Ruta ruta = rutaRepository.findBySolicitudTrasladoId(solicitudId)
                .orElseThrow(() -> new RuntimeException("No se encontró ruta para la solicitud ID: " + solicitudId));

        return rutaMapper.toDTO(ruta);
    }

    /**
     * Obtiene todos los tramos de una ruta
     */
    @Transactional(readOnly = true)
    public List<TramoDTO> obtenerTramosPorRuta(Long rutaId) {
        List<Tramo> tramos = tramoRepository.findByRutaId(rutaId);
        return tramos.stream()
                .map(tramoMapper::toDTO)
                .collect(Collectors.toList());
    }


    /**
     * Elimina una ruta y todos sus tramos
     */
    @Transactional
    public void eliminarRuta(Long rutaId) {
        if (!rutaRepository.existsById(rutaId)) {
            throw new RuntimeException("Ruta no encontrada con ID: " + rutaId);
        }
        rutaRepository.deleteById(rutaId);
        log.info("Ruta ID: {} eliminada junto con sus tramos", rutaId);
    }

}