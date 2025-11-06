package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.client.TarifaClient;
import back.tpi.ms_GestionDeOperaciones.domain.Cliente;
import back.tpi.ms_GestionDeOperaciones.domain.Contenedor;
import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import back.tpi.ms_GestionDeOperaciones.domain.SolicitudTraslado;
import back.tpi.ms_GestionDeOperaciones.dto.DistanciaResponse;
import back.tpi.ms_GestionDeOperaciones.dto.EstadoTransporteDTO;
import back.tpi.ms_GestionDeOperaciones.dto.SolicitudTrasladoDTO;
import back.tpi.ms_GestionDeOperaciones.dto.TarifaDTO;
import back.tpi.ms_GestionDeOperaciones.repository.SolicitudTrasladoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudTrasladoService {

    private final SolicitudTrasladoRepository repository;
    private final ClienteService clienteService;
    private final ContenedorService contenedorService;
    private final TarifaClient tarifaClient;
    private final DistanciaService distanciaService;


    // REQUERIMIENTO 1
    /**
     * Crea una nueva solicitud de traslado completa:
     * a) Crea el contenedor con peso y volumen (identificación única implícita en el ID)
     * b) Registra el cliente si no existe previamente
     * c) Las solicitudes registran estado PENDIENTE
     * d) Crea una nueva tarifa en el microservicio de tarifas
     */
    @Transactional
    public SolicitudTraslado crearSolicitudCompleta(SolicitudTrasladoDTO solicitudDTO) {
        log.info("Iniciando creación de solicitud de traslado completa");

        // b) REGISTRAR O VERIFICAR CLIENTE (LOCAL)
        Cliente cliente = clienteService.obtenerORegistrarCliente(solicitudDTO.getCliente());
        log.info("Cliente procesado con ID: {}", cliente.getId());

        // a) CREAR CONTENEDOR CON IDENTIFICACIÓN ÚNICA (peso y volumen)
        Contenedor contenedor = contenedorService.crearContenedor(solicitudDTO.getContenedor(), cliente);
        log.info("Contenedor creado con ID: {}, peso: {}kg, volumen: {}m³",
                contenedor.getId(), contenedor.getPeso(), contenedor.getVolumen());

        // d) CREAR TARIFA (REMOTO)
        TarifaDTO tarifaCreada = tarifaClient.crearTarifa(solicitudDTO.getTarifa());
        log.info("Tarifa creada con ID: {} en ms-GestionDeCostosYTarifas", tarifaCreada.getId());

        // CALCULAR DISTANCIA Y COSTOS
        DistanciaResponse resp = distanciaService.calcularDistancia(solicitudDTO.getCoordOrigenLat(),
                solicitudDTO.getCoordOrigenLng(),
                solicitudDTO.getCoordDestinoLat(), solicitudDTO.getCoordDestinoLng());

        double distancia = resp.getDistanciaKm();
        double tiempoEstimado = resp.getTiempoHoras();


        // El costo puede depender de distancia, peso y volumen
        Double costoEstimado = tarifaClient.calcularCostoEstimado(tarifaCreada.getId(), distancia);

        // c) CREAR SOLICITUD DE TRASLADO CON ESTADO PENDIENTE
        SolicitudTraslado solicitud = SolicitudTraslado.builder()
                .numero(generarNumeroSolicitud())
                .cliente(cliente)
                .contenedor(contenedor)
                .tarifaId(tarifaCreada.getId())
                .direccionOrigen(solicitudDTO.getDireccionOrigen())
                .coordOrigenLat(solicitudDTO.getCoordOrigenLat())
                .coordOrigenLng(solicitudDTO.getCoordOrigenLng())
                .direccionDestino(solicitudDTO.getDireccionDestino())
                .coordDestinoLat(solicitudDTO.getCoordDestinoLat())
                .coordDestinoLng(solicitudDTO.getCoordDestinoLng())
                .estado(EstadoSolicitud.PENDIENTE) // c) Estado inicial
                .costoEstimado(costoEstimado)
                .tiempoEstimado(tiempoEstimado)
                .fechaSolicitud(LocalDateTime.now())
                .build();

        SolicitudTraslado solicitudGuardada = repository.save(solicitud);
        log.info("Solicitud de traslado creada exitosamente con ID: {}, número: {} y estado: {}",
                solicitudGuardada.getId(),
                solicitudGuardada.getNumero(),
                solicitudGuardada.getEstado());

        return solicitudGuardada;
    }


    // REQUERIMIENTO 2
    /**
     * Consulta el estado del transporte por ID de solicitud
     */
    @Transactional(readOnly = true)
    public EstadoTransporteDTO consultarEstadoPorSolicitud(Long solicitudId) {
        SolicitudTraslado solicitud = obtenerPorId(solicitudId);
        return construirEstadoTransporteDTO(solicitud);
    }

    /**
     * Consulta el estado del transporte por número de solicitud
     */
    @Transactional(readOnly = true)
    public EstadoTransporteDTO consultarEstadoPorNumero(Integer numeroSolicitud) {
        SolicitudTraslado solicitud = obtenerPorNumero(numeroSolicitud);
        return construirEstadoTransporteDTO(solicitud);
    }

    /**
     * Consulta el estado del transporte por ID de contenedor
     */
    @Transactional(readOnly = true)
    public EstadoTransporteDTO consultarEstadoPorContenedor(Long contenedorId) {
        List<SolicitudTraslado> solicitudes = repository.findByContenedorId(contenedorId);

        if (solicitudes.isEmpty()) {
            throw new RuntimeException("No se encontró solicitud para el contenedor con ID: " + contenedorId);
        }

        // Retorna la solicitud más reciente
        SolicitudTraslado solicitudMasReciente = solicitudes.stream()
                .max((s1, s2) -> s1.getFechaSolicitud().compareTo(s2.getFechaSolicitud()))
                .orElseThrow();

        return construirEstadoTransporteDTO(solicitudMasReciente);
    }

    /**
     * Construye el DTO con toda la información del estado del transporte
     */
    private EstadoTransporteDTO construirEstadoTransporteDTO(SolicitudTraslado solicitud) {
        return EstadoTransporteDTO.builder()
                .solicitudId(solicitud.getId())
                .numeroSolicitud(solicitud.getNumero())
                .contenedorId(solicitud.getContenedor().getId())
                .estado(solicitud.getEstado())
                .clienteNombre(solicitud.getCliente().getNombre())
                .clienteEmail(solicitud.getCliente().getEmail())
                .contenedorPeso(solicitud.getContenedor().getPeso())
                .contenedorVolumen(solicitud.getContenedor().getVolumen())
                .direccionOrigen(solicitud.getDireccionOrigen())
                .direccionDestino(solicitud.getDireccionDestino())
                .fechaSolicitud(solicitud.getFechaSolicitud())
                .fechaInicio(solicitud.getFechaInicio())
                .fechaFinalizacion(solicitud.getFechaFinalizacion())
                .tiempoEstimado(solicitud.getTiempoEstimado())
                .tiempoReal(solicitud.getTiempoReal())
                .costoEstimado(solicitud.getCostoEstimado())
                .costoFinal(solicitud.getCostoFinal())
                .mensajeEstado(generarMensajeEstado(solicitud))
                .build();
    }

    /**
     * Genera un mensaje descriptivo según el estado actual
     */
    private String generarMensajeEstado(SolicitudTraslado solicitud) {
        switch (solicitud.getEstado()) {
            case PENDIENTE:
                return "Su solicitud está pendiente de aprobación.";
            case APROBADA:
                return "Su solicitud ha sido aprobada y está lista para iniciar el transporte.";
            case EN_PROCESO:
                return "Su contenedor está siendo transportado actualmente.";
            case COMPLETADA:
                return "El transporte ha sido completado exitosamente.";
            case CANCELADA:
                return "La solicitud de transporte ha sido cancelada.";
            default:
                return "Estado desconocido.";
        }
    }



    @Transactional(readOnly = true)
    public List<SolicitudTraslado> obtenerTodas() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public SolicitudTraslado obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public SolicitudTraslado obtenerPorNumero(Integer numero) {
        return repository.findByNumero(numero)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con número: " + numero));
    }

    @Transactional(readOnly = true)
    public List<SolicitudTraslado> obtenerPorEstado(EstadoSolicitud estado) {
        return repository.findByEstado(estado);
    }

    @Transactional
    public SolicitudTraslado actualizarEstado(Long id, EstadoSolicitud nuevoEstado) {
        SolicitudTraslado solicitud = obtenerPorId(id);
        solicitud.setEstado(nuevoEstado);

        // Actualizar fechas según el estado
        switch (nuevoEstado) {
            case EN_PROCESO:
                solicitud.setFechaInicio(LocalDateTime.now());
                break;
            case COMPLETADA:
            case CANCELADA:
                solicitud.setFechaFinalizacion(LocalDateTime.now());
                break;
        }

        return repository.save(solicitud);
    }

    @Transactional
    public SolicitudTraslado finalizarSolicitud(Long id, Double costoFinal, Double tiempoReal) {
        SolicitudTraslado solicitud = obtenerPorId(id);
        solicitud.setEstado(EstadoSolicitud.COMPLETADA);
        solicitud.setCostoFinal(costoFinal);
        solicitud.setTiempoReal(tiempoReal);
        solicitud.setFechaFinalizacion(LocalDateTime.now());

        return repository.save(solicitud);
    }

    @Transactional
    public void eliminarSolicitud(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Solicitud no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Genera un número único para la solicitud
     */
    private Integer generarNumeroSolicitud() {
        return repository.findAll().stream()
                .map(SolicitudTraslado::getNumero)
                .max(Integer::compareTo)
                .map(n -> n + 1)
                .orElse(1000);
    }

    /**
     * Calcula distancia entre coordenadas (fórmula de Haversine)
     */
    private Double calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 100.0; // Distancia por defecto
        }

        final int R = 6371; // Radio de la Tierra en km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}