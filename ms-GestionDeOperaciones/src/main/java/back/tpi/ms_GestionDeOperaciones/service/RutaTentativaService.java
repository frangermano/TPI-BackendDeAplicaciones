package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.client.DepositoClient;
import back.tpi.ms_GestionDeOperaciones.client.OsrmClient;
import back.tpi.ms_GestionDeOperaciones.client.TarifaClient;
import back.tpi.ms_GestionDeOperaciones.domain.*;
import back.tpi.ms_GestionDeOperaciones.dto.*;
import back.tpi.ms_GestionDeOperaciones.mapper.RutaMapper;
import back.tpi.ms_GestionDeOperaciones.repository.RutaRepository;
import back.tpi.ms_GestionDeOperaciones.repository.SolicitudTrasladoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RutaTentativaService {

    private final OsrmClient osrmClient;
    private final RutaRepository rutaRepository;
    private final SolicitudTrasladoRepository solicitudRepository;
    private final TarifaClient tarifaClient;
    private final DepositoClient depositoClient;
    private final RutaMapper rutaMapper;

    /**
     * Genera rutas tentativas dinámicamente con diferentes estrategias
     */
    public List<RutaTentativaDTO> consultarRutasTentativas(Long solicitudId) {
        SolicitudTraslado solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Validar que la solicitud esté en estado apropiado
        if (solicitud.getEstado() != EstadoSolicitud.APROBADA) {
            throw new RuntimeException("La solicitud debe estar APROBADA para consultar rutas");
        }

        List<RutaTentativaDTO> rutasTentativas = new ArrayList<>();

        // OPCIÓN 1: Ruta Directa (sin depósitos intermedios)
        RutaTentativaDTO rutaDirecta = generarRutaDirecta(solicitud);
        rutasTentativas.add(rutaDirecta);

        // OPCIÓN 2 y 3: Rutas con depósitos (si hay disponibles)
        try {
            // Obtener depósitos cercanos UNA SOLA VEZ
            List<DepositoDTO> depositosDisponibles = obtenerDepositosCercanosOptimizado(solicitud);

            if (!depositosDisponibles.isEmpty()) {
                log.info("✅ Encontrados {} depósitos disponibles", depositosDisponibles.size());

                // Ruta con 1 depósito
                RutaTentativaDTO rutaCon1Deposito = generarRutaConDepositos(
                        solicitud, depositosDisponibles, 1);
                if (rutaCon1Deposito != null) {
                    rutasTentativas.add(rutaCon1Deposito);
                }

                // Ruta con 2 depósitos (solo si hay al menos 2)
                if (depositosDisponibles.size() >= 2) {
                    RutaTentativaDTO rutaCon2Depositos = generarRutaConDepositos(
                            solicitud, depositosDisponibles, 2);
                    if (rutaCon2Depositos != null) {
                        rutasTentativas.add(rutaCon2Depositos);
                    }
                }
            } else {
                log.warn("⚠️ No hay depósitos disponibles para rutas alternativas");
            }
        } catch (Exception e) {
            log.error("❌ Error al obtener depósitos: {}", e.getMessage());
            // Continuar solo con ruta directa
        }

        // Numerar las opciones
        for (int i = 0; i < rutasTentativas.size(); i++) {
            rutasTentativas.get(i).setNumeroOpcion(i + 1);
        }

        log.info("✅ Generadas {} rutas tentativas", rutasTentativas.size());
        return rutasTentativas;
    }

    /**
     * Obtiene y filtra depósitos cercanos a la ruta de forma optimizada
     * Calcula distancias UNA SOLA VEZ usando Haversine (más rápido que OSRM)
     */
    private List<DepositoDTO> obtenerDepositosCercanosOptimizado(SolicitudTraslado solicitud) {
        // Obtener todos los depósitos
        List<DepositoDTO> todosLosDepositos = depositoClient.obtenerDepositosEnRuta(
                solicitud.getCoordOrigenLat(),
                solicitud.getCoordOrigenLng(),
                solicitud.getCoordDestinoLat(),
                solicitud.getCoordDestinoLng(),
                10 // Pedir más para tener opciones
        );

        if (todosLosDepositos.isEmpty()) {
            return List.of();
        }
        // 🔹 FILTRO NUEVO: eliminar depósitos demasiado cercanos al destino o al origen
        double DISTANCIA_MINIMA_KM = 5.0;

        todosLosDepositos = todosLosDepositos.stream()
                .filter(d -> {
                    double distanciaDestino = calcularDistanciaHaversine(
                            d.getLatitud(), d.getLongitud(),
                            solicitud.getCoordDestinoLat(), solicitud.getCoordDestinoLng()
                    );
                    double distanciaOrigen = calcularDistanciaHaversine(
                            d.getLatitud(), d.getLongitud(),
                            solicitud.getCoordOrigenLat(), solicitud.getCoordOrigenLng()
                    );
                    return distanciaDestino > DISTANCIA_MINIMA_KM && distanciaOrigen > DISTANCIA_MINIMA_KM;
                })
                .collect(Collectors.toList());

        log.info("🧭 Se filtraron depósitos muy cercanos (< {} km) al origen o destino. Quedan {} depósitos válidos.",
                DISTANCIA_MINIMA_KM, todosLosDepositos.size());


        // Calcular distancia de ruta directa
        double distanciaRutaDirecta = calcularDistanciaHaversine(
                solicitud.getCoordOrigenLat(),
                solicitud.getCoordOrigenLng(),
                solicitud.getCoordDestinoLat(),
                solicitud.getCoordDestinoLng()
        );

        // Calcular desviación para cada depósito usando Haversine (rápido)
        List<DepositoDTO> depositosConDistancias = todosLosDepositos.stream()
                .map(deposito -> {
                    double distDesdeOrigen = calcularDistanciaHaversine(
                            solicitud.getCoordOrigenLat(),
                            solicitud.getCoordOrigenLng(),
                            deposito.getLatitud(),
                            deposito.getLongitud()
                    );

                    double distHastaDestino = calcularDistanciaHaversine(
                            deposito.getLatitud(),
                            deposito.getLongitud(),
                            solicitud.getCoordDestinoLat(),
                            solicitud.getCoordDestinoLng()
                    );

                    double distTotal = distDesdeOrigen + distHastaDestino;
                    double desviacion = distTotal - distanciaRutaDirecta;

                    deposito.setDistanciaDesdeOrigen(redondear(distDesdeOrigen));
                    deposito.setDistanciaHastaDestino(redondear(distHastaDestino));
                    deposito.setDistanciaTotal(redondear(distTotal));
                    deposito.setDesviacionKm(redondear(desviacion));
                    deposito.setEnRuta(desviacion <= 100);

                    return deposito;
                })
                .filter(d -> d.getDesviacionKm() <= 150) // Filtrar grandes desviaciones
                .sorted(Comparator.comparing(DepositoDTO::getDesviacionKm))
                .limit(5) // Máximo 5 depósitos más cercanos
                .collect(Collectors.toList());

        log.info("Filtrados {} depósitos cercanos a la ruta", depositosConDistancias.size());
        return depositosConDistancias;
    }

    /**
     * Genera ruta directa sin depósitos
     */
    private RutaTentativaDTO generarRutaDirecta(SolicitudTraslado solicitud) {
        List<TramoTentativoDTO> tramos = new ArrayList<>();

        // Calcular distancia y tiempo origen -> destino con OSRM (1 llamada)
        DistanciaResponse resp = osrmClient.calcularDistancia(
                solicitud.getCoordOrigenLat(),
                solicitud.getCoordOrigenLng(),
                solicitud.getCoordDestinoLat(),
                solicitud.getCoordDestinoLng()
        );

        double distancia = resp.getDistanciaKm();
        double tiempoHoras = resp.getTiempoHoras();

        // Crear tramo único
        TramoTentativoDTO tramo = TramoTentativoDTO.builder()
                .origen(solicitud.getDireccionOrigen())
                .destino(solicitud.getDireccionDestino())
                .tipoTramo("TRANSPORTE")
                .distancia(distancia)
                .tiempoEstimado(resp.getTiempoLegible())
                .costoEstimado(0.0) // Se calcula después
                .coordOrigenLat(solicitud.getCoordOrigenLat())
                .coordOrigenLng(solicitud.getCoordOrigenLng())
                .coordDestinoLat(solicitud.getCoordDestinoLat())
                .coordDestinoLng(solicitud.getCoordDestinoLng())
                .build();

        tramos.add(tramo);

        // Calcular costo total
        double costoEstimado = tarifaClient.calcularCostoEstimado(
                solicitud.getTarifaId(),
                distancia
        );

        // Asignar costo al tramo
        tramo.setCostoEstimado(costoEstimado);

        return RutaTentativaDTO.builder()
                .descripcion("Ruta Directa - Sin depósitos intermedios")
                .cantidadTramos(1)
                .cantidadDepositos(0)
                .distanciaTotal(redondear(distancia))
                .tiempoEstimadoTotal(formatearTiempo(tiempoHoras))
                .costoEstimadoTotal(redondear(costoEstimado))
                .tramos(tramos)
                .ventajas(List.of(
                        "Menor tiempo de entrega",
                        "Ruta más simple",
                        "Menor costo operativo"
                ))
                .desventajas(List.of(
                        "Sin puntos de descanso",
                        "Mayor riesgo en caso de problemas"
                ))
                .build();
    }

    /**
     * Genera ruta con N depósitos intermedios
     * Recibe la lista de depósitos ya filtrados y ordenados
     */
    private RutaTentativaDTO generarRutaConDepositos(
            SolicitudTraslado solicitud,
            List<DepositoDTO> depositosDisponibles,
            int numeroDepositos) {

        if (depositosDisponibles.size() < numeroDepositos) {
            log.warn("No hay suficientes depósitos para generar ruta con {} depósitos", numeroDepositos);
            return null;
        }

        // Seleccionar los N primeros depósitos (ya están ordenados por cercanía)
        List<DepositoDTO> depositosSeleccionados = depositosDisponibles.stream()
                .limit(numeroDepositos)
                .collect(Collectors.toList());

        depositosSeleccionados.sort(Comparator.comparing(DepositoDTO::getDistanciaDesdeOrigen));

        log.info("🗺️ Orden de depósitos seleccionados (por distancia al origen): {}",
                depositosSeleccionados.stream().map(DepositoDTO::getDireccion).toList());

        List<TramoTentativoDTO> tramos = new ArrayList<>();
        double distanciaTotal = 0;
        double tiempoTotal = 0;
        double costoTotal = 0;

        // Primer tramo: Origen -> Primer Depósito (usar OSRM para ruta real)
        DepositoDTO primerDeposito = depositosSeleccionados.get(0);
        DistanciaResponse dist1 = osrmClient.calcularDistancia(
                solicitud.getCoordOrigenLat(),
                solicitud.getCoordOrigenLng(),
                primerDeposito.getLatitud(),
                primerDeposito.getLongitud()
        );

        TramoTentativoDTO tramo1 = crearTramoTransporte(
                solicitud.getDireccionOrigen(),
                primerDeposito.getDireccion(),
                solicitud.getCoordOrigenLat(),
                solicitud.getCoordOrigenLng(),
                primerDeposito.getLatitud(),
                primerDeposito.getLongitud(),
                dist1
        );

        double costoTramo1 = tarifaClient.calcularCostoEstimado(
                solicitud.getTarifaId(),
                dist1.getDistanciaKm()
        );
        tramo1.setCostoEstimado(costoTramo1);
        tramos.add(tramo1);

        distanciaTotal += dist1.getDistanciaKm();
        tiempoTotal += dist1.getTiempoHoras();
        costoTotal += costoTramo1;

        // Tramos entre depósitos
        for (int i = 0; i < depositosSeleccionados.size(); i++) {
            DepositoDTO depositoActual = depositosSeleccionados.get(i);

            // Agregar tramo de DEPOSITO (estadía)
            TramoTentativoDTO tramoDeposito = crearTramoDeposito(depositoActual);
            tramos.add(tramoDeposito);
            tiempoTotal += 0.5; // 30 minutos
            costoTotal += depositoActual.getCostoEstadia() != null ? depositoActual.getCostoEstadia() : 0.0;

            // Siguiente tramo
            if (i < depositosSeleccionados.size() - 1) {
                // Depósito actual -> Siguiente depósito
                DepositoDTO siguienteDeposito = depositosSeleccionados.get(i + 1);
                DistanciaResponse dist = osrmClient.calcularDistancia(
                        depositoActual.getLatitud(),
                        depositoActual.getLongitud(),
                        siguienteDeposito.getLatitud(),
                        siguienteDeposito.getLongitud()
                );

                TramoTentativoDTO tramoTransporte = crearTramoTransporte(
                        depositoActual.getDireccion(),
                        siguienteDeposito.getDireccion(),
                        depositoActual.getLatitud(),
                        depositoActual.getLongitud(),
                        siguienteDeposito.getLatitud(),
                        siguienteDeposito.getLongitud(),
                        dist
                );

                double costoTramo = tarifaClient.calcularCostoEstimado(
                        solicitud.getTarifaId(),
                        dist.getDistanciaKm()
                );
                tramoTransporte.setCostoEstimado(costoTramo);
                tramos.add(tramoTransporte);

                distanciaTotal += dist.getDistanciaKm();
                tiempoTotal += dist.getTiempoHoras();
                costoTotal += costoTramo;
            } else {
                // Último depósito -> Destino final
                DistanciaResponse distFinal = osrmClient.calcularDistancia(
                        depositoActual.getLatitud(),
                        depositoActual.getLongitud(),
                        solicitud.getCoordDestinoLat(),
                        solicitud.getCoordDestinoLng()
                );

                TramoTentativoDTO tramoFinal = crearTramoTransporte(
                        depositoActual.getDireccion(),
                        solicitud.getDireccionDestino(),
                        depositoActual.getLatitud(),
                        depositoActual.getLongitud(),
                        solicitud.getCoordDestinoLat(),
                        solicitud.getCoordDestinoLng(),
                        distFinal
                );

                double costoTramoFinal = tarifaClient.calcularCostoEstimado(
                        solicitud.getTarifaId(),
                        distFinal.getDistanciaKm()
                );
                tramoFinal.setCostoEstimado(costoTramoFinal);
                tramos.add(tramoFinal);

                distanciaTotal += distFinal.getDistanciaKm();
                tiempoTotal += distFinal.getTiempoHoras();
                costoTotal += costoTramoFinal;
            }
        }

        return RutaTentativaDTO.builder()
                .descripcion(String.format("Ruta con %d depósito(s) intermedio(s)", numeroDepositos))
                .cantidadTramos(tramos.size())
                .cantidadDepositos(numeroDepositos)
                .distanciaTotal(redondear(distanciaTotal))
                .tiempoEstimadoTotal(formatearTiempo(tiempoTotal))
                .costoEstimadoTotal(redondear(costoTotal))
                .tramos(tramos)
                .ventajas(List.of(
                        "Puntos de descanso disponibles",
                        "Mayor seguridad del contenedor",
                        "Flexibilidad ante imprevistos"
                ))
                .desventajas(List.of(
                        "Mayor tiempo total de entrega",
                        "Costos adicionales de manipulación y estadía"
                ))
                .build();
    }

    /**
     * Confirma y persiste la ruta tentativa seleccionada
     */
    @Transactional
    public RutaDTO confirmarRutaTentativa(Long solicitudId, int numeroOpcion) {
        // 1. Regenerar las rutas tentativas
        List<RutaTentativaDTO> rutasTentativas = consultarRutasTentativas(solicitudId);

        // 2. Validar que existe la opción seleccionada
        if (numeroOpcion < 1 || numeroOpcion > rutasTentativas.size()) {
            throw new RuntimeException("Opción de ruta inválida: " + numeroOpcion);
        }

        RutaTentativaDTO rutaSeleccionada = rutasTentativas.get(numeroOpcion - 1);

        // 3. Obtener la solicitud
        SolicitudTraslado solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // 4. Verificar que no tenga ya una ruta asignada
        if (rutaRepository.existsBySolicitudTrasladoId(solicitudId)) {
            throw new RuntimeException("La solicitud ya tiene una ruta asignada");
        }

        // 5. Crear la entidad Ruta
        Ruta ruta = Ruta.builder()
                .solicitudTraslado(solicitud)
                .cantidadTramos(0)
                .cantidadDepositos(0)
                .tramos(new ArrayList<>())
                .build();

        // 6. Convertir tramos tentativo a entidades Tramo
        for (TramoTentativoDTO tramoDTO : rutaSeleccionada.getTramos()) {
            Tramo tramo = Tramo.builder()
                    .origen(tramoDTO.getOrigen())
                    .destino(tramoDTO.getDestino())
                    .tipoTramo(tramoDTO.getTipoTramo())
                    .estado(EstadoTramo.PENDIENTE)
                    .costoAproximado(tramoDTO.getCostoEstimado() != null ?
                            tramoDTO.getCostoEstimado() : 0.0)
                    .coordOrigenLat(tramoDTO.getCoordOrigenLat())
                    .coordOrigenLng(tramoDTO.getCoordOrigenLng())
                    .coordDestinoLat(tramoDTO.getCoordDestinoLat())
                    .coordDestinoLng(tramoDTO.getCoordDestinoLng())
                    .ruta(ruta)
                    .distancia(tramoDTO.getDistancia())
                    .build();

            ruta.agregarTramo(tramo);
        }

        // 7. Calcular depósitos
        ruta.calcularCantidadDepositos();

        // 8. Actualizar solicitud con los datos calculados
        solicitud.setCostoEstimado(rutaSeleccionada.getCostoEstimadoTotal());
        solicitud.setTiempoEstimado(rutaSeleccionada.getTiempoEstimadoTotal());

        // 9. Guardar
        Ruta rutaGuardada = rutaRepository.save(ruta);
        solicitudRepository.save(solicitud);

        log.info("✅ Ruta confirmada y asignada a solicitud ID: {}", solicitudId);

        return rutaMapper.toDTO(rutaGuardada);
    }

    // ========== MÉTODOS AUXILIARES ==========

    private TramoTentativoDTO crearTramoTransporte(String origen, String destino,
                                                   Double latOrigen, Double lngOrigen,
                                                   Double latDestino, Double lngDestino,
                                                   DistanciaResponse distancia) {
        return TramoTentativoDTO.builder()
                .origen(origen)
                .destino(destino)
                .tipoTramo("TRANSPORTE")
                .distancia(distancia.getDistanciaKm())
                .tiempoEstimado(distancia.getTiempoLegible())
                .coordOrigenLat(latOrigen)
                .coordOrigenLng(lngOrigen)
                .coordDestinoLat(latDestino)
                .coordDestinoLng(lngDestino)
                .build();
    }

    private TramoTentativoDTO crearTramoDeposito(DepositoDTO deposito) {
        return TramoTentativoDTO.builder()
                .origen(deposito.getDireccion())
                .destino(deposito.getDireccion())
                .tipoTramo("DEPOSITO")
                .distancia(0.0)
                .tiempoEstimado("24:00 hs")
                .costoEstimado(deposito.getCostoEstadia() != null ? deposito.getCostoEstadia() : 0.0)
                .coordOrigenLat(deposito.getLatitud())
                .coordOrigenLng(deposito.getLongitud())
                .coordDestinoLat(deposito.getLatitud())
                .coordDestinoLng(deposito.getLongitud())
                .build();
    }

    /**
     * Fórmula de Haversine para calcular distancia entre dos puntos
     * Retorna distancia en kilómetros
     */
    private double calcularDistanciaHaversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int RADIO_TIERRA_KM = 6371;

        double latDistancia = Math.toRadians(lat2 - lat1);
        double lonDistancia = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistancia / 2) * Math.sin(latDistancia / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistancia / 2) * Math.sin(lonDistancia / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_KM * c;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private String formatearTiempo(double horas) {
        int h = (int) horas;
        int m = (int) Math.round((horas - h) * 60);
        return String.format("%d:%02d hs", h, m);
    }


}