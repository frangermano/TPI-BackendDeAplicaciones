package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.domain.*;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContenedorPendienteService {

    private final SolicitudTrasladoRepository solicitudRepository;
    private final RutaRepository rutaRepository;
    private final TramoRepository tramoRepository;

    /**
     * Consulta contenedores pendientes con filtros
     */
    @Transactional(readOnly = true)
    public List<ContenedorPendienteDTO> consultarContenedoresPendientes(
            FiltrosContenedorDTO filtros) {

        log.info("Consultando contenedores pendientes con filtros: {}", filtros);

        // 1. Obtener todas las solicitudes pendientes (no completadas ni canceladas)
        List<SolicitudTraslado> solicitudes = solicitudRepository.findAll().stream()
                .filter(s -> s.getEstado() != EstadoSolicitud.COMPLETADA &&
                        s.getEstado() != EstadoSolicitud.CANCELADA)
                .collect(Collectors.toList());

        // 2. Aplicar filtros si existen
        if (filtros != null) {
            solicitudes = aplicarFiltros(solicitudes, filtros);
        }

        // 3. Convertir a DTOs
        return solicitudes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }


    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Aplica los 3 filtros simples
     */
    private List<SolicitudTraslado> aplicarFiltros(List<SolicitudTraslado> solicitudes,
                                                   FiltrosContenedorDTO filtros) {
        return solicitudes.stream()
                // Filtros existentes
                .filter(s -> filtros.getEstado() == null || s.getEstado() == filtros.getEstado())
                .filter(s -> filtros.getClienteId() == null ||
                        s.getCliente().getId().equals(filtros.getClienteId()))

                // 1️⃣ Filtro por rango de fechas
                .filter(s -> filtros.getFechaSolicitudDesde() == null ||
                        s.getFechaSolicitud().isAfter(filtros.getFechaSolicitudDesde()))
                .filter(s -> filtros.getFechaSolicitudHasta() == null ||
                        s.getFechaSolicitud().isBefore(filtros.getFechaSolicitudHasta()))

                // 2️⃣ Filtros por peso y volumen
                .filter(s -> filtros.getPesoMinimo() == null ||
                        s.getContenedor().getPeso() >= filtros.getPesoMinimo())
                .filter(s -> filtros.getPesoMaximo() == null ||
                        s.getContenedor().getPeso() <= filtros.getPesoMaximo())
                .filter(s -> filtros.getVolumenMinimo() == null ||
                        s.getContenedor().getVolumen() >= filtros.getVolumenMinimo())
                .filter(s -> filtros.getVolumenMaximo() == null ||
                        s.getContenedor().getVolumen() <= filtros.getVolumenMaximo())

                // 3️⃣ Filtros por ubicación
                .filter(s -> filtros.getCiudadOrigen() == null ||
                        s.getDireccionOrigen().toLowerCase()
                                .contains(filtros.getCiudadOrigen().toLowerCase()))
                .filter(s -> filtros.getCiudadDestino() == null ||
                        s.getDireccionDestino().toLowerCase()
                                .contains(filtros.getCiudadDestino().toLowerCase()))

                .collect(Collectors.toList());
    }

    /**
     * Convierte solicitud a DTO simplificado
     */
    private ContenedorPendienteDTO convertirADTO(SolicitudTraslado solicitud) {
        Contenedor contenedor = solicitud.getContenedor();

        // Obtener información de ruta y tramo
        Optional<Ruta> rutaOpt = rutaRepository.findBySolicitudTrasladoId(solicitud.getId());
        Tramo tramoActual = obtenerTramoActual(solicitud);

        // Calcular ubicación actual
        String ubicacionActual = calcularUbicacionActual(solicitud, tramoActual);

        // Calcular progreso
        int totalTramos = 0;
        int tramosCompletados = 0;
        if (rutaOpt.isPresent()) {
            List<Tramo> tramos = rutaOpt.get().getTramos();
            totalTramos = tramos.size();
            tramosCompletados = (int) tramos.stream()
                    .filter(t -> t.getEstado() == EstadoTramo.COMPLETADO)
                    .count();
        }

        double porcentaje = totalTramos > 0 ?
                (tramosCompletados * 100.0) / totalTramos : 0.0;

        // Calcular fecha estimada
        LocalDateTime fechaEstimada = solicitud.getFechaInicio() != null &&
                solicitud.getTiempoEstimado() != null ?
                solicitud.getFechaInicio().plusHours(Long.parseLong(solicitud.getTiempoEstimado())) :
                null;

        return ContenedorPendienteDTO.builder()
                .contenedorId(contenedor.getId())
                .peso(contenedor.getPeso())
                .volumen(contenedor.getVolumen())
                .clienteNombre(solicitud.getCliente().getNombre())
                .solicitudId(solicitud.getId())
                .estado(solicitud.getEstado())
                .origen(solicitud.getDireccionOrigen())
                .destino(solicitud.getDireccionDestino())
                .ubicacionActual(ubicacionActual)
                .estadoDetallado(generarEstadoDetallado(solicitud, tramoActual))
                .tramosCompletados(tramosCompletados)
                .totalTramos(totalTramos)
                .porcentajeProgreso(Math.round(porcentaje * 100.0) / 100.0)
                .fechaSolicitud(solicitud.getFechaSolicitud())
                .fechaEstimadaEntrega(fechaEstimada)
                .costoEstimado(solicitud.getCostoEstimado())
                .build();
    }

    /**
     * Obtiene el tramo actual (en curso o próximo pendiente)
     */
    private Tramo obtenerTramoActual(SolicitudTraslado solicitud) {
        Optional<Ruta> rutaOpt = rutaRepository.findBySolicitudTrasladoId(solicitud.getId());
        if (!rutaOpt.isPresent()) {
            return null;
        }

        List<Tramo> tramos = rutaOpt.get().getTramos();

        // Buscar tramo en curso
        Optional<Tramo> enCurso = tramos.stream()
                .filter(t -> t.getEstado() == EstadoTramo.EN_CURSO)
                .findFirst();

        if (enCurso.isPresent()) {
            return enCurso.get();
        }

        // Si no hay en curso, buscar primer pendiente
        return tramos.stream()
                .filter(t -> t.getEstado() == EstadoTramo.PENDIENTE)
                .findFirst()
                .orElse(null);
    }

    /**
     * Calcula la ubicación actual descriptiva
     */
    private String calcularUbicacionActual(SolicitudTraslado solicitud, Tramo tramoActual) {
        if (tramoActual == null) {
            return solicitud.getDireccionOrigen();
        }

        if (tramoActual.getEstado() == EstadoTramo.EN_CURSO) {
            return "En tránsito: " + tramoActual.getOrigen() + " → " + tramoActual.getDestino();
        }

        if (tramoActual.getEstado() == EstadoTramo.PENDIENTE) {
            return tramoActual.getOrigen();
        }

        return "Ubicación desconocida";
    }

    /**
     * Genera descripción del estado
     */
    private String generarEstadoDetallado(SolicitudTraslado solicitud, Tramo tramoActual) {
        switch (solicitud.getEstado()) {
            case PENDIENTE:
                return "Solicitud pendiente de aprobación";
            case APROBADA:
                return "Aprobada - Esperando inicio de transporte";
            case EN_PROCESO:
                if (tramoActual == null) {
                    return "En proceso - Sin ruta asignada";
                }
                if (tramoActual.getEstado() == EstadoTramo.EN_CURSO) {
                    return "En tránsito hacia " + tramoActual.getDestino();
                }
                if (tramoActual.getEstado() == EstadoTramo.PENDIENTE) {
                    return "En espera en " + tramoActual.getOrigen();
                }
                return "En proceso";
            default:
                return "Estado desconocido";
        }
    }


}