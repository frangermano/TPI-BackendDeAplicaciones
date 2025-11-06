package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.domain.*;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ContenedorRepository contenedorRepository;

    /**
     * Consulta todos los contenedores pendientes con filtros
     */
    @Transactional(readOnly = true)
    public ConsultaContenedoresResponseDTO consultarContenedoresPendientes(FiltrosContenedorDTO filtros) {
        log.info("Consultando contenedores pendientes con filtros: {}", filtros);

        // 1. Obtener todas las solicitudes pendientes (no completadas ni canceladas)
        List<SolicitudTraslado> solicitudesPendientes = obtenerSolicitudesPendientes();

        // 2. Aplicar filtros
        List<SolicitudTraslado> solicitudesFiltradas = aplicarFiltros(solicitudesPendientes, filtros);

        // 3. Ordenar
        List<SolicitudTraslado> solicitudesOrdenadas = aplicarOrdenamiento(solicitudesFiltradas, filtros);

        // 4. Calcular paginación
        PaginacionResult paginacion = aplicarPaginacion(solicitudesOrdenadas, filtros);

        // 5. Convertir a DTOs
        List<ContenedorPendienteDTO> contenedores = paginacion.getSolicitudesPaginadas().stream()
                .map(this::convertirAContenedorPendienteDTO)
                .collect(Collectors.toList());

        // 6. Calcular estadísticas
        EstadisticasResult estadisticas = calcularEstadisticas(solicitudesFiltradas);

        // 7. Construir respuesta
        return ConsultaContenedoresResponseDTO.builder()
                .contenedores(contenedores)
                .totalContenedores(solicitudesFiltradas.size())
                .contenedoresEnTransito(estadisticas.getEnTransito())
                .contenedoresAtrasados(estadisticas.getAtrasados())
                .contenedoresEnDeposito(estadisticas.getEnDeposito())
                .contenedoresPorEstado(estadisticas.getPorEstado())
                .pesoTotal(estadisticas.getPesoTotal())
                .volumenTotal(estadisticas.getVolumenTotal())
                .pesoPromedio(estadisticas.getPesoPromedio())
                .volumenPromedio(estadisticas.getVolumenPromedio())
                .costoTotalEstimado(estadisticas.getCostoTotalEstimado())
                .costoTotalAcumulado(estadisticas.getCostoTotalAcumulado())
                .paginaActual(paginacion.getPaginaActual())
                .tamanoPagina(paginacion.getTamanoPagina())
                .totalPaginas(paginacion.getTotalPaginas())
                .totalRegistros(paginacion.getTotalRegistros())
                .filtrosAplicados(filtros)
                .fechaConsulta(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * Obtiene la ubicación actual de un contenedor específico
     */
    @Transactional(readOnly = true)
    public ContenedorPendienteDTO obtenerUbicacionContenedor(Long contenedorId) {
        log.info("Consultando ubicación del contenedor ID: {}", contenedorId);

        // Buscar la solicitud activa del contenedor
        List<SolicitudTraslado> solicitudes = solicitudRepository.findByContenedorId(contenedorId);

        SolicitudTraslado solicitudActiva = solicitudes.stream()
                .filter(s -> s.getEstado() != EstadoSolicitud.COMPLETADA &&
                        s.getEstado() != EstadoSolicitud.CANCELADA)
                .max(Comparator.comparing(SolicitudTraslado::getFechaSolicitud))
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró solicitud activa para el contenedor ID: " + contenedorId));

        return convertirAContenedorPendienteDTO(solicitudActiva);
    }

    /**
     * Obtiene estadísticas rápidas de contenedores pendientes
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasRapidas() {
        List<SolicitudTraslado> solicitudesPendientes = obtenerSolicitudesPendientes();
        EstadisticasResult stats = calcularEstadisticas(solicitudesPendientes);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalPendientes", solicitudesPendientes.size());
        resultado.put("enTransito", stats.getEnTransito());
        resultado.put("atrasados", stats.getAtrasados());
        resultado.put("enDeposito", stats.getEnDeposito());
        resultado.put("porEstado", stats.getPorEstado());
        resultado.put("pesoTotal", stats.getPesoTotal());
        resultado.put("volumenTotal", stats.getVolumenTotal());
        resultado.put("costoTotal", stats.getCostoTotalEstimado());

        return resultado;
    }

    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Obtiene todas las solicitudes que no están completadas ni canceladas
     */
    private List<SolicitudTraslado> obtenerSolicitudesPendientes() {
        List<SolicitudTraslado> todas = solicitudRepository.findAll();
        return todas.stream()
                .filter(s -> s.getEstado() != EstadoSolicitud.COMPLETADA &&
                        s.getEstado() != EstadoSolicitud.CANCELADA)
                .collect(Collectors.toList());
    }

    /**
     * Aplica filtros a la lista de solicitudes
     */
    private List<SolicitudTraslado> aplicarFiltros(List<SolicitudTraslado> solicitudes,
                                                   FiltrosContenedorDTO filtros) {
        if (filtros == null) {
            return solicitudes;
        }

        return solicitudes.stream()
                .filter(s -> filtros.getClienteId() == null ||
                        s.getCliente().getId().equals(filtros.getClienteId()))
                .filter(s -> filtros.getClienteNombre() == null ||
                        s.getCliente().getNombre().toLowerCase()
                                .contains(filtros.getClienteNombre().toLowerCase()))
                .filter(s -> filtros.getEstadoSolicitud() == null ||
                        s.getEstado() == filtros.getEstadoSolicitud())
                .filter(s -> filtros.getPesoMinimo() == null ||
                        s.getContenedor().getPeso() >= filtros.getPesoMinimo())
                .filter(s -> filtros.getPesoMaximo() == null ||
                        s.getContenedor().getPeso() <= filtros.getPesoMaximo())
                .filter(s -> filtros.getVolumenMinimo() == null ||
                        s.getContenedor().getVolumen() >= filtros.getVolumenMinimo())
                .filter(s -> filtros.getVolumenMaximo() == null ||
                        s.getContenedor().getVolumen() <= filtros.getVolumenMaximo())
                .filter(s -> filtros.getFechaSolicitudDesde() == null ||
                        !s.getFechaSolicitud().isBefore(filtros.getFechaSolicitudDesde()))
                .filter(s -> filtros.getFechaSolicitudHasta() == null ||
                        !s.getFechaSolicitud().isAfter(filtros.getFechaSolicitudHasta()))
                .filter(s -> filtros.getCiudadOrigen() == null ||
                        s.getDireccionOrigen().toLowerCase()
                                .contains(filtros.getCiudadOrigen().toLowerCase()))
                .filter(s -> filtros.getCiudadDestino() == null ||
                        s.getDireccionDestino().toLowerCase()
                                .contains(filtros.getCiudadDestino().toLowerCase()))
                .filter(s -> filtros.getCostoMinimo() == null ||
                        (s.getCostoEstimado() != null && s.getCostoEstimado() >= filtros.getCostoMinimo()))
                .filter(s -> filtros.getCostoMaximo() == null ||
                        (s.getCostoEstimado() != null && s.getCostoEstimado() <= filtros.getCostoMaximo()))
                .filter(s -> {
                    if (filtros.getSoloAtrasados() == null || !filtros.getSoloAtrasados()) {
                        return true;
                    }
                    return esAtrasado(s);
                })
                .filter(s -> {
                    if (filtros.getEstadoTramo() == null) {
                        return true;
                    }
                    Tramo tramoActual = obtenerTramoActual(s);
                    return tramoActual != null && tramoActual.getEstado() == filtros.getEstadoTramo();
                })
                .filter(s -> {
                    if (filtros.getCamionId() == null) {
                        return true;
                    }
                    Tramo tramoActual = obtenerTramoActual(s);
                    return tramoActual != null && filtros.getCamionId().equals(tramoActual.getCamionId());
                })
                .collect(Collectors.toList());
    }

    /**
     * Aplica ordenamiento a la lista de solicitudes
     */
    private List<SolicitudTraslado> aplicarOrdenamiento(List<SolicitudTraslado> solicitudes,
                                                        FiltrosContenedorDTO filtros) {
        if (filtros == null || filtros.getOrdenarPor() == null) {
            return solicitudes;
        }

        Comparator<SolicitudTraslado> comparator;

        switch (filtros.getOrdenarPor().toLowerCase()) {
            case "peso":
                comparator = Comparator.comparing(s -> s.getContenedor().getPeso());
                break;
            case "volumen":
                comparator = Comparator.comparing(s -> s.getContenedor().getVolumen());
                break;
            case "costo":
                comparator = Comparator.comparing(SolicitudTraslado::getCostoEstimado,
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "progreso":
                comparator = Comparator.comparing(this::calcularProgreso);
                break;
            case "fecha":
            default:
                comparator = Comparator.comparing(SolicitudTraslado::getFechaSolicitud);
                break;
        }

        // Aplicar dirección
        if ("DESC".equalsIgnoreCase(filtros.getDireccionOrden())) {
            comparator = comparator.reversed();
        }

        return solicitudes.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Aplica paginación
     */
    private PaginacionResult aplicarPaginacion(List<SolicitudTraslado> solicitudes,
                                               FiltrosContenedorDTO filtros) {
        int pagina = (filtros != null && filtros.getPagina() != null) ? filtros.getPagina() : 0;
        int tamanoPagina = (filtros != null && filtros.getTamanoPagina() != null) ?
                filtros.getTamanoPagina() : 20;

        int totalRegistros = solicitudes.size();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / tamanoPagina);

        int inicio = pagina * tamanoPagina;
        int fin = Math.min(inicio + tamanoPagina, totalRegistros);

        List<SolicitudTraslado> paginadas = inicio < totalRegistros ?
                solicitudes.subList(inicio, fin) :
                Collections.emptyList();

        return PaginacionResult.builder()
                .solicitudesPaginadas(paginadas)
                .paginaActual(pagina)
                .tamanoPagina(tamanoPagina)
                .totalPaginas(totalPaginas)
                .totalRegistros((long) totalRegistros)
                .build();
    }

    /**
     * Convierte una solicitud a DTO de contenedor pendiente
     */
    private ContenedorPendienteDTO convertirAContenedorPendienteDTO(SolicitudTraslado solicitud) {
        Contenedor contenedor = solicitud.getContenedor();
        Cliente cliente = solicitud.getCliente();

        // Obtener información de la ruta
        Optional<Ruta> rutaOpt = rutaRepository.findBySolicitudTrasladoId(solicitud.getId());
        Ruta ruta = rutaOpt.orElse(null);

        // Obtener tramo actual
        Tramo tramoActual = obtenerTramoActual(solicitud);

        // Calcular progreso
        ProgresoInfo progreso = calcularProgresoDetallado(ruta);

        // Calcular ubicación actual
        UbicacionInfo ubicacion = calcularUbicacionActual(solicitud, ruta, tramoActual);

        // Calcular fecha estimada de entrega
        LocalDateTime fechaEstimada = calcularFechaEstimadaEntrega(solicitud);

        // Calcular costo acumulado
        Double costoAcumulado = calcularCostoAcumulado(ruta);

        return ContenedorPendienteDTO.builder()
                .contenedorId(contenedor.getId())
                .peso(contenedor.getPeso())
                .volumen(contenedor.getVolumen())
                .clienteId(cliente.getId())
                .clienteNombre(cliente.getNombre())
                .clienteEmail(cliente.getEmail())
                .solicitudId(solicitud.getId())
                .numeroSolicitud(solicitud.getNumero())
                .estadoSolicitud(solicitud.getEstado())
                .direccionOrigen(solicitud.getDireccionOrigen())
                .direccionDestino(solicitud.getDireccionDestino())
                .ubicacionActual(ubicacion.getDescripcion())
                .latitudActual(ubicacion.getLatitud())
                .longitudActual(ubicacion.getLongitud())
                .rutaId(ruta != null ? ruta.getId() : null)
                .tramoActualOrden(tramoActual != null ? obtenerOrdenTramo(tramoActual) : null)
                .tramoActualOrigen(tramoActual != null ? tramoActual.getOrigen() : null)
                .tramoActualDestino(tramoActual != null ? tramoActual.getDestino() : null)
                .tramoActualEstado(tramoActual != null ? tramoActual.getEstado() : null)
                .totalTramos(progreso.getTotalTramos())
                .tramosCompletados(progreso.getTramosCompletados())
                .porcentajeCompletado(progreso.getPorcentaje())
                .fechaSolicitud(solicitud.getFechaSolicitud())
                .fechaInicioTransporte(solicitud.getFechaInicio())
                .fechaEstimadaEntrega(fechaEstimada)
                .costoEstimado(solicitud.getCostoEstimado())
                .costoAcumulado(costoAcumulado)
                .camionAsignadoId(tramoActual != null ? tramoActual.getCamionId() : null)
                .estadoDetallado(generarEstadoDetallado(solicitud, tramoActual))
                .diasEnTransito(calcularDiasEnTransito(solicitud))
                .atrasado(esAtrasado(solicitud))
                .build();
    }

    /**
     * Obtiene el tramo actual (en curso o el próximo pendiente)
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

        // Si no hay en curso, buscar el primer pendiente
        return tramos.stream()
                .filter(t -> t.getEstado() == EstadoTramo.PENDIENTE)
                .findFirst()
                .orElse(null);
    }

    /**
     * Calcula el progreso detallado de una ruta
     */
    private ProgresoInfo calcularProgresoDetallado(Ruta ruta) {
        if (ruta == null) {
            return ProgresoInfo.builder()
                    .totalTramos(0)
                    .tramosCompletados(0)
                    .porcentaje(0.0)
                    .build();
        }

        List<Tramo> tramos = ruta.getTramos();
        long completados = tramos.stream()
                .filter(t -> t.getEstado() == EstadoTramo.COMPLETADO)
                .count();

        double porcentaje = tramos.size() > 0 ?
                (completados * 100.0) / tramos.size() : 0.0;

        return ProgresoInfo.builder()
                .totalTramos(tramos.size())
                .tramosCompletados((int) completados)
                .porcentaje(Math.round(porcentaje * 100.0) / 100.0)
                .build();
    }

    /**
     * Calcula la ubicación actual del contenedor
     */
    private UbicacionInfo calcularUbicacionActual(SolicitudTraslado solicitud, Ruta ruta, Tramo tramoActual) {
        if (tramoActual == null) {
            // Si no hay tramo actual, está en el origen
            return UbicacionInfo.builder()
                    .descripcion(solicitud.getDireccionOrigen())
                    .latitud(solicitud.getCoordOrigenLat())
                    .longitud(solicitud.getCoordOrigenLng())
                    .build();
        }

        if (tramoActual.getEstado() == EstadoTramo.EN_CURSO) {
            // Si está en curso, puede estar en ruta o aproximándose al destino del tramo
            return UbicacionInfo.builder()
                    .descripcion("En tránsito: " + tramoActual.getOrigen() + " → " + tramoActual.getDestino())
                    .latitud(tramoActual.getCoordOrigenLat())
                    .longitud(tramoActual.getCoordOrigenLng())
                    .build();
        }

        if (tramoActual.getEstado() == EstadoTramo.PENDIENTE) {
            // Si está pendiente, está en el origen del tramo
            return UbicacionInfo.builder()
                    .descripcion(tramoActual.getOrigen())
                    .latitud(tramoActual.getCoordOrigenLat())
                    .longitud(tramoActual.getCoordOrigenLng())
                    .build();
        }

        return UbicacionInfo.builder()
                .descripcion("Ubicación desconocida")
                .latitud(null)
                .longitud(null)
                .build();
    }

    /**
     * Calcula la fecha estimada de entrega
     */
    private LocalDateTime calcularFechaEstimadaEntrega(SolicitudTraslado solicitud) {
        if (solicitud.getFechaInicio() == null) {
            // Si no ha iniciado, estimar desde ahora
            return LocalDateTime.now().plusHours(solicitud.getTiempoEstimado().longValue());
        }

        return solicitud.getFechaInicio().plusHours(solicitud.getTiempoEstimado().longValue());
    }

    /**
     * Calcula el costo acumulado hasta el momento
     */
    private Double calcularCostoAcumulado(Ruta ruta) {
        if (ruta == null) {
            return 0.0;
        }

        return ruta.getTramos().stream()
                .filter(t -> t.getEstado() == EstadoTramo.COMPLETADO)
                .mapToDouble(t -> t.getCostoReal() != null ? t.getCostoReal() : 0.0)
                .sum();
    }

    /**
     * Genera descripción detallada del estado
     */
    private String generarEstadoDetallado(SolicitudTraslado solicitud, Tramo tramoActual) {
        if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {
            return "Solicitud pendiente de aprobación";
        }

        if (solicitud.getEstado() == EstadoSolicitud.APROBADA) {
            return "Solicitud aprobada - Esperando inicio de transporte";
        }

        if (tramoActual == null) {
            return "En proceso - Sin ruta asignada";
        }

        switch (tramoActual.getEstado()) {
            case PENDIENTE:
                return "En espera - Próximo tramo: " + tramoActual.getDestino();
            case EN_CURSO:
                return "En tránsito hacia " + tramoActual.getDestino();
            case COMPLETADO:
                return "Todos los tramos completados - Llegando a destino";
            case CANCELADO:
                return "Tramo cancelado - Requiere atención";
            default:
                return "Estado desconocido";
        }
    }

    /**
     * Calcula días en tránsito
     */
    private Integer calcularDiasEnTransito(SolicitudTraslado solicitud) {
        if (solicitud.getFechaInicio() == null) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(solicitud.getFechaInicio(), LocalDateTime.now());
    }

    /**
     * Determina si un envío está atrasado
     */
    private boolean esAtrasado(SolicitudTraslado solicitud) {
        if (solicitud.getFechaInicio() == null || solicitud.getTiempoEstimado() == null) {
            return false;
        }

        LocalDateTime fechaEstimadaEntrega = solicitud.getFechaInicio()
                .plusHours(solicitud.getTiempoEstimado().longValue());

        return LocalDateTime.now().isAfter(fechaEstimadaEntrega);
    }

    /**
     * Calcula el progreso de una solicitud
     */
    private Double calcularProgreso(SolicitudTraslado solicitud) {
        Optional<Ruta> rutaOpt = rutaRepository.findBySolicitudTrasladoId(solicitud.getId());
        if (!rutaOpt.isPresent()) {
            return 0.0;
        }

        return calcularProgresoDetallado(rutaOpt.get()).getPorcentaje();
    }

    /**
     * Obtiene el orden de un tramo en su ruta
     */
    private Integer obtenerOrdenTramo(Tramo tramo) {
        List<Tramo> tramos = tramo.getRuta().getTramos();
        return tramos.indexOf(tramo) + 1;
    }

    /**
     * Calcula estadísticas generales
     */
    private EstadisticasResult calcularEstadisticas(List<SolicitudTraslado> solicitudes) {
        int enTransito = 0;
        int atrasados = 0;
        int enDeposito = 0;
        Map<String, Integer> porEstado = new HashMap<>();

        double pesoTotal = 0.0;
        double volumenTotal = 0.0;
        double costoTotal = 0.0;
        double costoAcumulado = 0.0;

        for (SolicitudTraslado s : solicitudes) {
            // Contar por estado
            String estado = s.getEstado().toString();
            porEstado.put(estado, porEstado.getOrDefault(estado, 0) + 1);

            // En tránsito
            if (s.getEstado() == EstadoSolicitud.EN_PROCESO) {
                enTransito++;
            }

            // Atrasados
            if (esAtrasado(s)) {
                atrasados++;
            }

            // En depósito
            Tramo tramoActual = obtenerTramoActual(s);
            if (tramoActual != null && "DEPOSITO".equals(tramoActual.getTipoTramo())) {
                enDeposito++;
            }

            // Sumas
            pesoTotal += s.getContenedor().getPeso();
            volumenTotal += s.getContenedor().getVolumen();
            if (s.getCostoEstimado() != null) {
                costoTotal += s.getCostoEstimado();
            }

            Optional<Ruta> ruta = rutaRepository.findBySolicitudTrasladoId(s.getId());
            if (ruta.isPresent()) {
                costoAcumulado += calcularCostoAcumulado(ruta.get());
            }
        }

        int count = solicitudes.size();
        double pesoPromedio = count > 0 ? pesoTotal / count : 0.0;
        double volumenPromedio = count > 0 ? volumenTotal / count : 0.0;

        return EstadisticasResult.builder()
                .enTransito(enTransito)
                .atrasados(atrasados)
                .enDeposito(enDeposito)
                .porEstado(porEstado)
                .pesoTotal(Math.round(pesoTotal * 100.0) / 100.0)
                .volumenTotal(Math.round(volumenTotal * 100.0) / 100.0)
                .pesoPromedio(Math.round(pesoPromedio * 100.0) / 100.0)
                .volumenPromedio(Math.round(volumenPromedio * 100.0) / 100.0)
                .costoTotalEstimado(Math.round(costoTotal * 100.0) / 100.0)
                .costoTotalAcumulado(Math.round(costoAcumulado * 100.0) / 100.0)
                .build();
    }

    // ========== CLASES INTERNAS AUXILIARES ==========

    @Data
    @Builder
    private static class ProgresoInfo {
        private Integer totalTramos;
        private Integer tramosCompletados;
        private Double porcentaje;
    }

    @Data
    @Builder
    private static class UbicacionInfo {
        private String descripcion;
        private Double latitud;
        private Double longitud;
    }

    @Data
    @Builder
    private static class PaginacionResult {
        private List<SolicitudTraslado> solicitudesPaginadas;
        private Integer paginaActual;
        private Integer tamanoPagina;
        private Integer totalPaginas;
        private Long totalRegistros;
    }

    @Data
    @Builder
    private static class EstadisticasResult {
        private Integer enTransito;
        private Integer atrasados;
        private Integer enDeposito;
        private Map<String, Integer> porEstado;
        private Double pesoTotal;
        private Double volumenTotal;
        private Double pesoPromedio;
        private Double volumenPromedio;
        private Double costoTotalEstimado;
        private Double costoTotalAcumulado;
    }
}
