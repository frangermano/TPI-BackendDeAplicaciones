package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.client.OsrmClient;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    private final OsrmClient osrmClient;


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
        DistanciaResponse resp = osrmClient.calcularDistancia(solicitudDTO.getCoordOrigenLat(),
                solicitudDTO.getCoordOrigenLng(),
                solicitudDTO.getCoordDestinoLat(), solicitudDTO.getCoordDestinoLng());

        double distancia = resp.getDistanciaKm();
        String distanciaLegible = resp.getDistanciaLegible();
        double tiempoEstimado = resp.getTiempoHoras();
        String tiempoEstimadoLegible = resp.getTiempoLegible();
        log.info("Distancia: {}", distancia);



        // El costo puede depender de distancia, peso y volumen
        Double costoEstimado = tarifaClient.calcularCostoEstimado(tarifaCreada.getId(), distancia);

        // c) CREAR SOLICITUD DE TRASLADO CON ESTADO PENDIENTE
        SolicitudTraslado solicitud = SolicitudTraslado.builder()
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
                .tiempoEstimado(tiempoEstimadoLegible)
                .fechaSolicitud(LocalDateTime.now())
                .build();

        SolicitudTraslado solicitudGuardada = repository.save(solicitud);
        log.info("Solicitud de traslado creada exitosamente con ID: {} y estado: {}",
                solicitudGuardada.getId(),
                solicitudGuardada.getEstado());

        return solicitudGuardada;
    }


    // REQUERIMIENTO 2
    /**
     * Consulta el estado del transporte por ID de solicitud
     */
    @Transactional(readOnly = true)
    public EstadoTransporteDTO consultarEstadoPorSolicitud(Long solicitudId) {
        log.info("Consultando estado para solicitud ID: {}", solicitudId);

        SolicitudTraslado solicitud = repository.findById(solicitudId)
                .orElseThrow(() -> {
                    log.error("Solicitud no encontrada con ID: {}", solicitudId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Solicitud no encontrada con id: " + solicitudId
                    );
                });

        log.info("Solicitud encontrada: ID={}, Estado={}", solicitud.getId(), solicitud.getEstado());

        // Validar que tenga cliente y contenedor
        if (solicitud.getCliente() == null) {
            log.error("La solicitud {} no tiene cliente asociado", solicitudId);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Datos incompletos: solicitud sin cliente"
            );
        }

        if (solicitud.getContenedor() == null) {
            log.error("La solicitud {} no tiene contenedor asociado", solicitudId);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Datos incompletos: solicitud sin contenedor"
            );
        }

        return construirEstadoTransporteDTO(solicitud);
    }


    @Transactional(readOnly = true)
    public EstadoTransporteDTO consultarEstadoPorContenedor(Long contenedorId) {
        log.info("Consultando estado para contenedor ID: {}", contenedorId);
        List<SolicitudTraslado> solicitudes = repository.findByContenedorId(contenedorId);

        if (solicitudes.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontró solicitud para el contenedor con ID: " + contenedorId
            );
        }

        SolicitudTraslado solicitudMasReciente = solicitudes.stream()
                .max((s1, s2) -> s1.getFechaSolicitud().compareTo(s2.getFechaSolicitud()))
                .orElseThrow();

        if (solicitudMasReciente.getCliente() == null || solicitudMasReciente.getContenedor() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Datos incompletos en la solicitud"
            );
        }

        return construirEstadoTransporteDTO(solicitudMasReciente);
    }

    /**
     * Construye el DTO con información del estado del transporte
     * Solo incluye campos que NUNCA son null o tienen valores por defecto seguros
     */
    private EstadoTransporteDTO construirEstadoTransporteDTO(SolicitudTraslado solicitud) {
        log.debug("Construyendo EstadoTransporteDTO para solicitud ID: {}", solicitud.getId());

        Cliente cliente = solicitud.getCliente();
        Contenedor contenedor = solicitud.getContenedor();

        return EstadoTransporteDTO.builder()
                // IDs y datos básicos (SIEMPRE presentes)
                .solicitudId(solicitud.getId())
                .estado(solicitud.getEstado())

                // Cliente (validado previamente)
                .clienteNombre(cliente.getNombre())
                .clienteEmail(cliente.getEmail())

                // Contenedor (validado previamente)
                .contenedorId(contenedor.getId())
                .contenedorPeso(contenedor.getPeso())
                .contenedorVolumen(contenedor.getVolumen())

                // Direcciones (SIEMPRE presentes por nullable=false)
                .direccionOrigen(solicitud.getDireccionOrigen())
                .direccionDestino(solicitud.getDireccionDestino())

                // Fecha de solicitud (SIEMPRE presente)
                .fechaSolicitud(solicitud.getFechaSolicitud())

                // Fechas opcionales (pueden ser null)
                //.fechaInicio(solicitud.getFechaInicio())
                //.fechaFinalizacion(solicitud.getFechaFinalizacion())

                // Tiempos y costos opcionales (pueden ser null)
                //.tiempoEstimado(solicitud.getTiempoEstimado())
               // .tiempoReal(solicitud.getTiempoReal())
                //.costoEstimado(solicitud.getCostoEstimado())
                //.costoFinal(solicitud.getCostoFinal())

                // Mensaje descriptivo
                .mensajeEstado(generarMensajeEstado(solicitud))
                .build();
    }

    private String generarMensajeEstado(SolicitudTraslado solicitud) {
        return switch (solicitud.getEstado()) {
            case PENDIENTE -> "Su solicitud está pendiente de aprobación.";
            case APROBADA -> "Su solicitud ha sido aprobada y está lista para iniciar el transporte.";
            case EN_PROCESO -> "Su contenedor está siendo transportado actualmente.";
            case COMPLETADA -> "El transporte ha sido completado exitosamente.";
            case CANCELADA -> "La solicitud de transporte ha sido cancelada.";
            default -> "Estado desconocido.";
        };
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
    public SolicitudTraslado finalizarSolicitud(Long id, Double costoFinal, String tiempoReal) {
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