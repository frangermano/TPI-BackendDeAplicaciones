package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.domain.*;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroTramoService {

    private final TramoRepository tramoRepository;
    private final RutaRepository rutaRepository;
    private final SolicitudTrasladoRepository solicitudRepository;

    /**
     * Registra el INICIO de un tramo por el transportista
     */
    @Transactional
    public RegistroTramoResponseDTO registrarInicioTramo(RegistroTramoDTO registroDTO) {
        log.info("Transportista {} registrando INICIO del tramo ID: {}",
                registroDTO.getTransportistaId(), registroDTO.getTramoId());

        // 1. Obtener el tramo
        Tramo tramo = tramoRepository.findById(registroDTO.getTramoId())
                .orElseThrow(() -> new RuntimeException(
                        "Tramo no encontrado con ID: " + registroDTO.getTramoId()));

        // 2. Validar que el tramo está PENDIENTE
        if (tramo.getEstado() != EstadoTramo.PENDIENTE) {
            throw new RuntimeException(
                    "El tramo debe estar en estado PENDIENTE para iniciar. Estado actual: " +
                            tramo.getEstado());
        }

        // 3. Validar que tiene camión asignado
        if (tramo.getCamionPatente() == null) {
            throw new RuntimeException("El tramo no tiene camión asignado");
        }

        EstadoTramo estadoAnterior = tramo.getEstado();

        // 5. Actualizar estado y fecha de inicio
        tramo.setEstado(EstadoTramo.EN_CURSO);
        tramo.setFechaHoraInicio(LocalDateTime.now());

        Tramo tramoActualizado = tramoRepository.save(tramo);
        log.info("✅ Tramo ID: {} iniciado exitosamente a las {}",
                tramo.getId(), tramo.getFechaHoraInicio());

        // 6. Actualizar estado de la solicitud si es el primer tramo
        actualizarEstadoSolicitudAlIniciar(tramo);

        // 7. Construir respuesta
        return construirRespuesta(tramoActualizado, estadoAnterior,
                "Tramo iniciado exitosamente", false);
    }

    /**
     * Registra el FIN de un tramo por el transportista
     */
    @Transactional
    public RegistroTramoResponseDTO registrarFinTramo(RegistroTramoDTO registroDTO) {
        log.info("Transportista {} registrando FIN del tramo ID: {}",
                registroDTO.getTransportistaId(), registroDTO.getTramoId());

        // 1. Obtener el tramo
        Tramo tramo = tramoRepository.findById(registroDTO.getTramoId())
                .orElseThrow(() -> new RuntimeException(
                        "Tramo no encontrado con ID: " + registroDTO.getTramoId()));

        // 2. Validar que el tramo está EN_CURSO
        if (tramo.getEstado() != EstadoTramo.EN_CURSO) {
            throw new RuntimeException(
                    "El tramo debe estar EN_CURSO para finalizarlo. Estado actual: " +
                            tramo.getEstado());
        }

        // 3. Validar que tiene fecha de inicio
        if (tramo.getFechaHoraInicio() == null) {
            throw new RuntimeException("El tramo no tiene fecha de inicio registrada");
        }

        EstadoTramo estadoAnterior = tramo.getEstado();

        // 4. Actualizar estado y fecha de fin
        tramo.setEstado(EstadoTramo.COMPLETADO);
        tramo.setFechaHoraFin(LocalDateTime.now());

        Tramo tramoActualizado = tramoRepository.save(tramo);

        // Calcular duración
        Duration duracion = Duration.between(tramo.getFechaHoraInicio(), tramo.getFechaHoraFin());
        double horas = duracion.toMinutes() / 60.0;

        log.info("✅ Tramo ID: {} finalizado exitosamente. Duración: {} horas",
                tramo.getId(), String.format("%.2f", horas));

        // 5. Verificar si todos los tramos de la ruta están completados
        boolean todosCompletados = verificarTodosLosTramosCompletados(tramo.getRuta());

        // 6. Actualizar estado de la solicitud si todos los tramos están completados
        if (todosCompletados) {
            actualizarEstadoSolicitudAlFinalizar(tramo);
        }

        // 7. Construir respuesta
        return construirRespuesta(tramoActualizado, estadoAnterior,
                "Tramo finalizado exitosamente", true);
    }

    /**
     * Obtiene el estado actual de un tramo
     */
    @Transactional(readOnly = true)
    public TramoDTO obtenerEstadoTramo(Long tramoId) {
        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + tramoId));

        return convertirATramoDTO(tramo);
    }



    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Actualiza el estado de la solicitud cuando inicia el primer tramo
     */
    private void actualizarEstadoSolicitudAlIniciar(Tramo tramo) {
        Ruta ruta = tramo.getRuta();
        SolicitudTraslado solicitud = ruta.getSolicitudTraslado();

        // Si es el primer tramo, actualizar solicitud a EN_PROCESO
        if (solicitud.getEstado() == EstadoSolicitud.APROBADA) {
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
            solicitud.setFechaInicio(LocalDateTime.now());
            solicitudRepository.save(solicitud);
            log.info("Solicitud ID: {} actualizada a EN_PROCESO", solicitud.getId());
        }
    }

    /**
     * Verifica si todos los tramos de una ruta están completados
     */
    private boolean verificarTodosLosTramosCompletados(Ruta ruta) {
        return ruta.getTramos().stream()
                .allMatch(t -> t.getEstado() == EstadoTramo.COMPLETADO);
    }

    /**
     * Actualiza el estado de la solicitud cuando todos los tramos están completados
     */
    private void actualizarEstadoSolicitudAlFinalizar(Tramo tramo) {
        Ruta ruta = tramo.getRuta();
        SolicitudTraslado solicitud = ruta.getSolicitudTraslado();

        // Calcular tiempo real total
        LocalDateTime inicio = solicitud.getFechaInicio();
        LocalDateTime fin = LocalDateTime.now();
        Duration duracion = Duration.between(inicio, fin);
        double horasReales = duracion.toMinutes() / 60.0;

        // Calcular costo real total
        double costoRealTotal = ruta.getTramos().stream()
                .mapToDouble(t -> t.getCostoReal() != null ? t.getCostoReal() : 0.0)
                .sum();

        solicitud.setEstado(EstadoSolicitud.COMPLETADA);
        solicitud.setFechaFinalizacion(fin);
        solicitud.setTiempoReal(String.valueOf(horasReales));
        solicitud.setCostoFinal(costoRealTotal);

        solicitudRepository.save(solicitud);
        log.info("✅ Solicitud ID: {} COMPLETADA. Tiempo real: {} horas, Costo final: ${}",
                solicitud.getId(), String.format("%.2f", horasReales), costoRealTotal);
    }

    /**
     * Construye la respuesta del registro
     */
    private RegistroTramoResponseDTO construirRespuesta(Tramo tramo, EstadoTramo estadoAnterior,
                                                        String mensaje, boolean tramoCompletado) {
        Ruta ruta = tramo.getRuta();
        List<Tramo> todosLosTramos = ruta.getTramos();

        long tramosCompletados = todosLosTramos.stream()
                .filter(t -> t.getEstado() == EstadoTramo.COMPLETADO)
                .count();

        boolean todosCompletados = tramosCompletados == todosLosTramos.size();

        Double duracionHoras = null;
        if (tramo.getFechaHoraInicio() != null && tramo.getFechaHoraFin() != null) {
            Duration duracion = Duration.between(tramo.getFechaHoraInicio(), tramo.getFechaHoraFin());
            duracionHoras = duracion.toMinutes() / 60.0;
        }

        return RegistroTramoResponseDTO.builder()
                .tramoId(tramo.getId())
                .origen(tramo.getOrigen())
                .destino(tramo.getDestino())
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(tramo.getEstado())
                .fechaHoraInicio(tramo.getFechaHoraInicio())
                .fechaHoraFin(tramo.getFechaHoraFin())
                .duracionHoras(duracionHoras)
                .mensaje(mensaje)
                .tramoCompletado(tramoCompletado)
                .rutaId(ruta.getId())
                .tramosCompletados((int) tramosCompletados)
                .totalTramos(todosLosTramos.size())
                .todosLosTramosCompletados(todosCompletados)
                .build();
    }

    /**
     * Convierte Tramo a DTO
     */
    private TramoDTO convertirATramoDTO(Tramo tramo) {
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
                .build();
    }
}