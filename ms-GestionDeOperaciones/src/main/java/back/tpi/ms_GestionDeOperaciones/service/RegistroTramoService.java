package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.client.CamionClient;
import back.tpi.ms_GestionDeOperaciones.client.OsrmClient;
import back.tpi.ms_GestionDeOperaciones.client.TarifaClient;
import back.tpi.ms_GestionDeOperaciones.domain.*;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroTramoService {

    private final TramoRepository tramoRepository;
    private final RutaRepository rutaRepository;
    private final SolicitudTrasladoRepository solicitudRepository;
    private final CamionClient camionClient;
    private final OsrmClient osrmClient;
    private final TarifaClient tarifaClient;

    /**
     * Registra el INICIO de un tramo por el transportista
     * ✅ VALIDACIÓN 2: Marca el camión como NO DISPONIBLE
     * ✅ VALIDACIÓN 3: Verifica que el tramo anterior esté completado
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

        // 4. ✅ VALIDACIÓN 3: Verificar que el tramo anterior (si existe) esté completado
        validarTramoAnteriorCompletado(tramo);

        EstadoTramo estadoAnterior = tramo.getEstado();

        // 5. ✅ VALIDACIÓN 2: Marcar camión como NO DISPONIBLE
        try {
            camionClient.actualizarDisponibilidad(tramo.getCamionPatente(), false);
            log.info("✅ Camión {} marcado como NO DISPONIBLE", tramo.getCamionPatente());
        } catch (Exception e) {
            log.error("Error al marcar camión como no disponible: {}", e.getMessage());
            throw new RuntimeException(
                    "Error al actualizar disponibilidad del camión: " + e.getMessage());
        }

        // 6. Actualizar estado y fecha de inicio
        tramo.setEstado(EstadoTramo.EN_CURSO);
        tramo.setFechaHoraInicio(LocalDateTime.now());

        Tramo tramoActualizado = tramoRepository.save(tramo);
        log.info("✅ Tramo ID: {} iniciado exitosamente a las {}",
                tramo.getId(), tramo.getFechaHoraInicio());

        // 7. Actualizar estado de la solicitud si es el primer tramo
        actualizarEstadoSolicitudAlIniciar(tramo);

        // 8. Construir respuesta
        return construirRespuesta(tramoActualizado, estadoAnterior,
                "Tramo iniciado exitosamente. Camión marcado como no disponible.", false);
    }

    /**
     * ✅ VALIDACIÓN 3: Verifica que el tramo anterior de la misma ruta esté completado
     */
    private void validarTramoAnteriorCompletado(Tramo tramoActual) {
        Ruta ruta = tramoActual.getRuta();
        List<Tramo> todosLosTramos = ruta.getTramos();

        // Ordenar tramos por ID (asumiendo que se crearon en orden)
        todosLosTramos.sort(Comparator.comparing(Tramo::getId));

        // Encontrar el índice del tramo actual
        int indiceActual = -1;
        for (int i = 0; i < todosLosTramos.size(); i++) {
            if (todosLosTramos.get(i).getId().equals(tramoActual.getId())) {
                indiceActual = i;
                break;
            }
        }

        if (indiceActual == -1) {
            throw new RuntimeException("No se pudo determinar la posición del tramo en la ruta");
        }

        // Si no es el primer tramo, validar que el anterior esté completado
        if (indiceActual > 0) {
            Tramo tramoAnterior = todosLosTramos.get(indiceActual - 1);

            if (tramoAnterior.getEstado() != EstadoTramo.COMPLETADO) {
                throw new RuntimeException(
                        String.format("No se puede iniciar el tramo '%s → %s'. " +
                                        "El tramo anterior '%s → %s' debe estar COMPLETADO. Estado actual: %s",
                                tramoActual.getOrigen(), tramoActual.getDestino(),
                                tramoAnterior.getOrigen(), tramoAnterior.getDestino(),
                                tramoAnterior.getEstado()));
            }

            log.info("✅ Validación exitosa: Tramo anterior ID {} está COMPLETADO",
                    tramoAnterior.getId());
        } else {
            log.info("✅ Es el primer tramo de la ruta, no requiere validación de tramo anterior");
        }
    }

    /**
     * Registra el FIN de un tramo por el transportista
     * ✅ VALIDACIÓN 2: Marca el camión como DISPONIBLE nuevamente
     * ✅ Calcula el costo real del tramo
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

        // Calcular duración
        Duration duracion = Duration.between(tramo.getFechaHoraInicio(), tramo.getFechaHoraFin());
        double horas = duracion.toMinutes() / 60.0;

        // 5. 💰 CALCULAR COSTO REAL DEL TRAMO
        double costoReal = calcularCostoRealTramo(tramo);
        tramo.setCostoReal(costoReal);

        log.info("💰 Costo real calculado para tramo ID {}: ${}", tramo.getId(), costoReal);

        Tramo tramoActualizado = tramoRepository.save(tramo);

        log.info("✅ Tramo ID: {} finalizado exitosamente. Duración: {} horas, Costo real: ${}",
                tramo.getId(), String.format("%.2f", horas), costoReal);

        // 6. ✅ VALIDACIÓN 2: Marcar camión como DISPONIBLE nuevamente
        if (tramo.getCamionPatente() != null) {
            try {
                camionClient.actualizarDisponibilidad(tramo.getCamionPatente(), true);
                log.info("✅ Camión {} marcado como DISPONIBLE nuevamente",
                        tramo.getCamionPatente());
            } catch (Exception e) {
                log.error("Error al marcar camión como disponible: {}", e.getMessage());
                // No lanzar excepción aquí para no bloquear la finalización del tramo
            }
        }

        // 7. Verificar si todos los tramos de la ruta están completados
        boolean todosCompletados = verificarTodosLosTramosCompletados(tramo.getRuta());

        // 8. Actualizar estado de la solicitud si todos los tramos están completados
        if (todosCompletados) {
            actualizarEstadoSolicitudAlFinalizar(tramo);
        }

        // 9. Construir respuesta
        return construirRespuesta(tramoActualizado, estadoAnterior,
                "Tramo finalizado exitosamente. Camión marcado como disponible.", true);
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
     * Calcula el costo real de un tramo basado en:
     * - Distancia recorrida (entre coordenadas del tramo)
     * - Costo por km del camión
     * - Consumo de combustible del camión
     * - Precio del combustible de la tarifa
     * - Cargo de gestión de la tarifa (si aplica)
     */
    private double calcularCostoRealTramo(Tramo tramo) {
        if (tramo.getFechaHoraInicio() == null || tramo.getFechaHoraFin() == null) {
            log.warn("No se puede calcular el costo real del tramo ID {} porque faltan fechas.", tramo.getId());
            return 0.0;
        }

        switch (tramo.getTipoTramo().toUpperCase()) {
            case "TRANSPORTE":
                CamionDTO camion = camionClient.obtenerCamionPorPatente(tramo.getCamionPatente());
                TarifaDTO tarifaDTO = tarifaClient.getTarifa(tramo.getRuta().getSolicitudTraslado().getTarifaId());

                double distancia = 0.0;

                if (tramo.getCoordOrigenLat() != null && tramo.getCoordOrigenLng() != null &&
                        tramo.getCoordDestinoLat() != null && tramo.getCoordDestinoLng() != null) {

                    // Usar OSRM para obtener la distancia real de la ruta
                    DistanciaResponse distanciaResponse = osrmClient.calcularDistancia(
                            tramo.getCoordOrigenLat(),
                            tramo.getCoordOrigenLng(),
                            tramo.getCoordDestinoLat(),
                            tramo.getCoordDestinoLng()
                    );
                    distancia = distanciaResponse.getDistanciaKm();

                    log.info("📏 Distancia real calculada para tramo {}: {} km",
                            tramo.getId(), distancia);
                } else {
                    log.warn("⚠️ Tramo {} sin coordenadas, usando distancia aproximada", tramo.getId());
                    distancia = 50.0; // Distancia por defecto si no hay coordenadas
                }

                log.info("🧾 Calcular costo TRANSPORTE: tramoID={}, distancia={}, costoKm={}, valorCombustible={}",
                        tramo.getId(),
                        distancia,
                        camion != null ? camion.getCostoKm() : null,
                        tarifaDTO != null ? tarifaDTO.getValorCombustibleLitro() : null);


                double costoOperativo = distancia * camion.getCostoKm();
                double consumoTotal = distancia * (camion.getCostoCombustible() / 100.0);
                double costoCombustible = consumoTotal * tarifaDTO.getValorCombustibleLitro();

                double costoReal = costoOperativo + costoCombustible;

                log.info("✅ Costo real calculado tramo {} = {}", tramo.getId(), costoReal);
                return costoReal;

            case "DEPOSITO":
                // Calcular cantidad de días de estadía
                long diasEstadia = ChronoUnit.DAYS.between(
                        tramo.getFechaHoraInicio().toLocalDate(),
                        tramo.getFechaHoraFin().toLocalDate()
                );
                if (diasEstadia == 0) diasEstadia = 1; // mínimo un día
                double costoPorDia = tramo.getCostoAproximado() != null ? tramo.getCostoAproximado() : 0.0;
                return costoPorDia * diasEstadia;

            default:
                return 0.0;
        }
    }

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
            log.info("✅ Solicitud ID: {} actualizada a EN_PROCESO", solicitud.getId());
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
        solicitud.setTiempoReal(String.format("%.2f", horasReales));
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

        /*
        Double duracionHoras = null;
        if (tramo.getFechaHoraInicio() != null && tramo.getFechaHoraFin() != null) {
            Duration duracion = Duration.between(tramo.getFechaHoraInicio(), tramo.getFechaHoraFin());
            duracionHoras = duracion.toMinutes() / 60.0;
        }

         */
        String duracionLegible = "";
        if (tramo.getFechaHoraInicio() != null && tramo.getFechaHoraFin() != null) {
            Duration duracion = Duration.between(tramo.getFechaHoraInicio(), tramo.getFechaHoraFin());
            long horas = duracion.toHours();
            long minutos = duracion.toMinutesPart();
            duracionLegible = String.format("%dh %02dm", horas, minutos);
        }

        return RegistroTramoResponseDTO.builder()
                .tramoId(tramo.getId())
                .origen(tramo.getOrigen())
                .destino(tramo.getDestino())
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(tramo.getEstado())
                .fechaHoraInicio(tramo.getFechaHoraInicio())
                .fechaHoraFin(tramo.getFechaHoraFin())
                .duracionHoras(duracionLegible)
                .mensaje(mensaje)
                .tramoCompletado(tramoCompletado)
                .rutaId(ruta.getId())
                .tramosCompletados((int) tramosCompletados)
                .totalTramos(todosLosTramos.size())
                .todosLosTramosCompletados(todosCompletados)
                .costoReal(tramo.getCostoReal())
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