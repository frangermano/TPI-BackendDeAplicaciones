package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.client.TarifaClient;
import back.tpi.ms_GestionDeOperaciones.dto.DistanciaResponse;
import back.tpi.ms_GestionDeOperaciones.domain.Ruta;
import back.tpi.ms_GestionDeOperaciones.domain.SolicitudTraslado;
import back.tpi.ms_GestionDeOperaciones.domain.Tramo;
import back.tpi.ms_GestionDeOperaciones.repository.RutaRepository;
import back.tpi.ms_GestionDeOperaciones.repository.SolicitudTrasladoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class RutaTentativaService {

    private final DistanciaService distanciaService;
    private final RutaRepository rutaRepository;
    private final SolicitudTrasladoRepository solicitudRepository;
    private final TarifaClient tarifaClient;


    // REQUERIMIENTO 3
    /**
     * Consulta las rutas tentativas asociadas a una solicitud de traslado,
     * y calcula para cada una sus tramos con distancia, duración y costo estimado.
     * No asigna ninguna ruta — el usuario elige manualmente cuál usar.
     */
    public List<Ruta> consultarRutasTentativas(Long solicitudId) {
        // Cargar la solicitud para obtener datos necesarios (p.ej. tarifaId)
        SolicitudTraslado solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        List<Ruta> rutasTentativas = rutaRepository
                .findAllBySolicitudTrasladoId(solicitudId);

        for (Ruta ruta : rutasTentativas) {
            double distanciaTotal = 0;
            double duracionTotal = 0;

            for (Tramo tramo : ruta.getTramos()) {
                DistanciaResponse respuesta = distanciaService.calcularDistancia(
                        tramo.getCoordOrigenLat(),
                        tramo.getCoordOrigenLng(),
                        tramo.getCoordDestinoLat(),
                        tramo.getCoordDestinoLng()
                );

                distanciaTotal += respuesta.getDistanciaKm();
                duracionTotal += respuesta.getTiempoHoras();

                // Si la entidad Tramo posee campos para distancia/tiempo, sería buena idea setearlos acá.
                // Ej: tramo.setDistanciaKm(respuesta.getDistanciaKm());
                // Ej: tramo.setTiempoHoras(respuesta.getTiempoHoras());
            }

            // Calcular costo estimado usando la tarifa de la solicitud
            double costoEstimado = tarifaClient.calcularCostoEstimado(solicitud.getTarifaId(), distanciaTotal);

            // No modificar la entidad SolicitudTraslado aquí: las rutas tentativas son sólo propuestas.
            // La solicitud NO debe ser mutada hasta que el usuario confirme y se llame a asignarRutaASolicitud.
            // Si quieres que el resultado incluya resúmenes, setéalos en la entidad Ruta (si existen setters)
            // o construye y retorna DTOs (recomendado). Ejemplos comentados:
            // Ej: ruta.setDistanciaTotal(distanciaTotal);
            // Ej: ruta.setTiempoEstimado(duracionTotal);
            // Ej: ruta.setCostoEstimado(costoEstimado);

            // Logueamos los valores calculados para depuración sin mutar la solicitud
            log.debug("Ruta tentative id={} distanciaTotal={} km tiempoTotal={} h costoEstimado={}",
                    ruta.getId(), distanciaTotal, duracionTotal, costoEstimado);
        }

        return rutasTentativas;
    }

    // REQUERIMIENTO 4
    /**
     * Asigna manualmente una ruta seleccionada por el usuario a la solicitud de traslado.
     * La ruta y la solicitud deben existir previamente.
     */
    public SolicitudTraslado asignarRutaASolicitud(Long idSolicitud, Long idRuta) {
        SolicitudTraslado solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Ruta rutaSeleccionada = rutaRepository.findById(idRuta)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        // Se asigna la ruta elegida por el usuario
        solicitud.setRuta(rutaSeleccionada);
        return solicitudRepository.save(solicitud);
    }
}
