package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.client.TarifaClient;
import back.tpi.ms_GestionDeOperaciones.domain.*;
import back.tpi.ms_GestionDeOperaciones.dto.CostoDetalleDTO;
import back.tpi.ms_GestionDeOperaciones.dto.DistanciaResponse;
import back.tpi.ms_GestionDeOperaciones.dto.TarifaDTO;
import back.tpi.ms_GestionDeOperaciones.repository.SolicitudTrasladoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculoCostoService {

    private final TarifaClient tarifaClient;
    private final SolicitudTrasladoRepository solicitudRepository;
    private final DistanciaService distanciaService;

    // Constantes para el cálculo
    private static final double CONSUMO_COMBUSTIBLE_POR_KM = 0.35; // litros por km (ajustar según tipo de camión)

    @Transactional
    public CostoDetalleDTO calcularYActualizarCosto(Long solicitudId) {
        log.info("Calculando costo total para solicitud ID: {}", solicitudId);

        // 1. Obtener la solicitud
        SolicitudTraslado solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + solicitudId));

        // 2. Obtener la tarifa desde ms-GestionDeInfraestructura
        TarifaDTO tarifa = tarifaClient.getTarifa(solicitud.getTarifaId());
        log.info("Tarifa obtenida: {} - {}", tarifa.getId(), tarifa.getNombre());

        // 3. Calcular distancia total
        DistanciaResponse distanciaYTiempo = calcularDistanciaTotal(solicitud);
        double distanciaTotal = distanciaYTiempo.getDistanciaKm();
        log.info("Distancia total calculada: {} km", distanciaTotal);

        // 4. Calcular horas de estadía en depósitos
        double horasEstadia = calcularHorasEstadia(solicitud.getRuta());
        log.info("Horas de estadía: {} horas", horasEstadia);

        // 5. Obtener datos del contenedor
        Contenedor contenedor = solicitud.getContenedor();

        // 6. Calcular costos parciales
        double costoCombustible = calcularCostoCombustible(distanciaTotal, tarifa);
        double costoEstadia = calcularCostoEstadia(horasEstadia, tarifa);
        double costoPeso = calcularCostoPeso(contenedor, tarifa);
        double costoVolumen = calcularCostoVolumen(contenedor, tarifa);
        double cargoGestion = tarifa.getCargoGestionTrama();

        // 7. Calcular costo total
        double costoTotal = costoCombustible + costoEstadia + costoPeso +
                costoVolumen + cargoGestion;

        // 8. Actualizar la solicitud
        solicitud.setCostoFinal(costoTotal);
        solicitudRepository.save(solicitud);

        log.info("Costo total calculado y guardado: ${}", costoTotal);

        // 9. Retornar detalle completo
        return CostoDetalleDTO.builder()
                .distanciaTotal(distanciaTotal)
                .horasEstadia(horasEstadia)
                .pesoContenedor(contenedor.getPeso())
                .volumenContenedor(contenedor.getVolumen())
                .costoCombustible(costoCombustible)
                .costoEstadia(costoEstadia)
                .costoPeso(costoPeso)
                .costoVolumen(costoVolumen)
                .cargoGestion(cargoGestion)
                .costoTotal(costoTotal)
                .nombreTarifa(tarifa.getNombre())
                .tarifaId(tarifa.getId())
                .build();
    }

    /**
     * Calcula la distancia total de la ruta (origen → depósitos → destino)
     */
    private DistanciaResponse calcularDistanciaTotal(SolicitudTraslado solicitud) {
        Ruta ruta = solicitud.getRuta();

        if (ruta == null || ruta.getTramos() == null || ruta.getTramos().isEmpty()) {
            // Si no hay tramos definidos, calcular distancia directa
            return distanciaService.calcularDistancia(
                    solicitud.getCoordOrigenLat(),
                    solicitud.getCoordOrigenLng(),
                    solicitud.getCoordDestinoLat(),
                    solicitud.getCoordDestinoLng()
            );
        }

        /*
        // Sumar distancias de todos los tramos
        return ruta.getTramos().stream()
                .mapToDouble(Tramo::getDistancia)
                .sum();

         */
        return null;
    }

    /**
     * Calcula las horas totales de estadía en depósitos
     */
    private double calcularHorasEstadia(Ruta ruta) {
        if (ruta == null || ruta.getTramos() == null) {
            return 0.0;
        }

        return ruta.getTramos().stream()
                .filter(tramo -> tramo.getEstado() == EstadoTramo.COMPLETADO)
                .filter(tramo -> tramo.getFechaHoraFin() != null &&
                        tramo.getFechaHoraInicio() != null)
                .mapToDouble(this::calcularHorasTramo)
                .sum();
    }

    /**
     * Calcula las horas entre llegada y salida de un tramo
     */
    private double calcularHorasTramo(Tramo tramo) {
        LocalDateTime llegada = tramo.getFechaHoraFin();
        LocalDateTime salida = tramo.getFechaHoraInicio();

        Duration duration = Duration.between(llegada, salida);
        return duration.toMinutes() / 60.0; // Convertir a horas con decimales
    }

    /**
     * Calcula el costo de combustible
     */
    private double calcularCostoCombustible(double distanciaKm, TarifaDTO tarifa) {
        double litrosConsumidos = distanciaKm * CONSUMO_COMBUSTIBLE_POR_KM;
        return litrosConsumidos * tarifa.getValorCombustibleLitro();
    }

    /**
     * Calcula el costo de estadía en depósitos
     */
    private double calcularCostoEstadia(double horas, TarifaDTO tarifa) {
        return horas * tarifa.getCostoPorHoraEstadia();
    }

    /**
     * Calcula el costo por peso del contenedor
     */
    private double calcularCostoPeso(Contenedor contenedor, TarifaDTO tarifa) {
        /*
        if (contenedor.getPeso() == null) {
            return 0.0;
        }

         */
        return contenedor.getPeso() * tarifa.getFactorPeso();
    }

    /**
     * Calcula el costo por volumen del contenedor
     */
    private double calcularCostoVolumen(Contenedor contenedor, TarifaDTO tarifa) {
        /*
        if (contenedor.getVolumen() == null) {
            return 0.0;
        }

         */
        return contenedor.getVolumen() * tarifa.getFactorVolumen();
    }
}