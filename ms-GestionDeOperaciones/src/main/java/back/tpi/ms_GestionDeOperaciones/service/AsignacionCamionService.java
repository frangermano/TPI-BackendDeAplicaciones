package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.client.CamionClient;
import back.tpi.ms_GestionDeOperaciones.domain.*;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsignacionCamionService {

    private final TramoRepository tramoRepository;
    private final RutaRepository rutaRepository;
    private final SolicitudTrasladoRepository solicitudRepository;
    private final CamionClient camionClient;

    /**
     * Asigna un camión a un tramo validando capacidad Y DISPONIBILIDAD
     */
    @Transactional
    public TramoDTO asignarCamionATramo(AsignarCamionDTO asignacionDTO) {
        log.info("Asignando camión {} al tramo ID: {}",
                asignacionDTO.getPatenteCamion(), asignacionDTO.getTramoId());

        // 1. Validar que el tramo existe y está pendiente
        Tramo tramo = tramoRepository.findById(asignacionDTO.getTramoId())
                .orElseThrow(() -> new RuntimeException(
                        "Tramo no encontrado con ID: " + asignacionDTO.getTramoId()));

        if (tramo.getEstado() != EstadoTramo.PENDIENTE) {
            throw new RuntimeException(
                    "Solo se puede asignar camión a tramos en estado PENDIENTE. Estado actual: " +
                            tramo.getEstado());
        }

        // 2. Obtener información del camión desde el microservicio
        CamionDTO camion;
        try {
            camion = camionClient.obtenerCamionPorPatente(asignacionDTO.getPatenteCamion());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al obtener información del camión con patente: " +
                            asignacionDTO.getPatenteCamion(), e);
        }

        // 3. ✅ VALIDACIÓN 1: Verificar que el camión está disponible
        if (!camion.getDisponible()) {
            throw new RuntimeException(
                    "El camión con patente " + asignacionDTO.getPatenteCamion() +
                            " no está disponible. Está actualmente asignado a otro tramo.");
        }

        // 4. Validar capacidad del camión (si está habilitado)
        boolean validarCapacidad = asignacionDTO.getValidarCapacidad() == null ||
                asignacionDTO.getValidarCapacidad();

        if (validarCapacidad) {
            validarCapacidadCamion(tramo, camion);
        }

        // 5. Asignar camión al tramo
        tramo.setCamionPatente(camion.getPatente());
        Tramo tramoActualizado = tramoRepository.save(tramo);

        log.info("✅ Camión {} asignado exitosamente al tramo ID: {}",
                asignacionDTO.getPatenteCamion(), tramo.getId());

        return convertirATramoDTO(tramoActualizado);
    }

    /**
     * Valida que el camión tenga capacidad suficiente para el contenedor
     */
    private void validarCapacidadCamion(Tramo tramo, CamionDTO camion) {
        log.info("Validando capacidad del camión {} para el tramo ID: {}",
                camion.getPatente(), tramo.getId());

        // Obtener el contenedor de la ruta/solicitud
        Ruta ruta = tramo.getRuta();
        SolicitudTraslado solicitud = ruta.getSolicitudTraslado();
        Contenedor contenedor = solicitud.getContenedor();

        TipoCamionDTO tipoCamion = camion.getTipoCamion();

        // Validar peso
        if (contenedor.getPeso() > tipoCamion.getCapacidadPeso()) {
            throw new RuntimeException(
                    String.format("El camión no tiene capacidad de peso suficiente. " +
                                    "Contenedor: %.2f kg, Capacidad camión: %.2f kg",
                            contenedor.getPeso(), tipoCamion.getCapacidadPeso()));
        }

        // Validar volumen
        if (contenedor.getVolumen() > tipoCamion.getCapacidadVolumen()) {
            throw new RuntimeException(
                    String.format("El camión no tiene capacidad de volumen suficiente. " +
                                    "Contenedor: %.2f m³, Capacidad camión: %.2f m³",
                            contenedor.getVolumen(), tipoCamion.getCapacidadVolumen()));
        }

        log.info("✅ Validación de capacidad exitosa. Peso: {}/{} kg, Volumen: {}/{} m³",
                contenedor.getPeso(), tipoCamion.getCapacidadPeso(),
                contenedor.getVolumen(), tipoCamion.getCapacidadVolumen());
    }

    /**
     * Obtiene camiones disponibles que pueden transportar un contenedor específico
     */
    @Transactional(readOnly = true)
    public List<CamionDTO> obtenerCamionesDisponiblesParaContenedor(Long contenedorId) {
        log.info("Buscando camiones disponibles para contenedor ID: {}", contenedorId);

        // Obtener información del contenedor
        Contenedor contenedor = obtenerContenedorPorId(contenedorId);

        // Obtener todos los camiones disponibles
        List<CamionDTO> camionesDisponibles = camionClient.obtenerCamionesDisponibles();

        // Filtrar por capacidad
        return camionesDisponibles.stream()
                .filter(camion -> camion.getTipoCamion().getCapacidadPeso() >= contenedor.getPeso())
                .filter(camion -> camion.getTipoCamion().getCapacidadVolumen() >= contenedor.getVolumen())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene camiones disponibles para un tramo específico
     */
    @Transactional(readOnly = true)
    public List<CamionDTO> obtenerCamionesDisponiblesParaTramo(Long tramoId) {
        log.info("Buscando camiones disponibles para tramo ID: {}", tramoId);

        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + tramoId));

        // Obtener contenedor asociado al tramo
        Ruta ruta = tramo.getRuta();
        SolicitudTraslado solicitud = ruta.getSolicitudTraslado();
        Contenedor contenedor = solicitud.getContenedor();

        // Obtener camiones disponibles que puedan transportar este contenedor
        List<CamionDTO> camionesDisponibles = camionClient.obtenerCamionesDisponibles();

        return camionesDisponibles.stream()
                .filter(camion -> camion.getTipoCamion().getCapacidadPeso() >= contenedor.getPeso())
                .filter(camion -> camion.getTipoCamion().getCapacidadVolumen() >= contenedor.getVolumen())
                .collect(Collectors.toList());
    }

    /**
     * Libera un camión de un tramo (marca como disponible nuevamente)
     */
    @Transactional
    public TramoDTO liberarCamionDeTramo(Long tramoId) {
        log.info("Liberando camión del tramo ID: {}", tramoId);

        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado con ID: " + tramoId));

        if (tramo.getCamionPatente() == null) {
            throw new RuntimeException("El tramo no tiene camión asignado");
        }

        String patente = tramo.getCamionPatente();

        // Marcar camión como disponible en el microservicio
        try {
            camionClient.actualizarDisponibilidad(patente, true);
        } catch (Exception e) {
            log.error("Error al liberar camión {}: {}", patente, e.getMessage());
        }

        tramo.setCamionPatente(null);
        Tramo tramoActualizado = tramoRepository.save(tramo);

        log.info("✅ Camión {} liberado exitosamente del tramo ID: {}", patente, tramoId);

        return convertirATramoDTO(tramoActualizado);
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Obtiene un contenedor por ID
     */
    private Contenedor obtenerContenedorPorId(Long contenedorId) {
        List<SolicitudTraslado> solicitudes = solicitudRepository.findByContenedorId(contenedorId);
        if (solicitudes.isEmpty()) {
            throw new RuntimeException("Contenedor no encontrado con ID: " + contenedorId);
        }
        return solicitudes.get(0).getContenedor();
    }

    /**
     * Convierte Tramo a DTO
     */
    private TramoDTO convertirATramoDTO(Tramo tramo) {
        return TramoDTO.builder()
                .tramoId(tramo.getId())
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