package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.client.TarifaClient;
import back.tpi.ms_GestionDeOperaciones.dto.DistanciaResponse;
import back.tpi.ms_GestionDeOperaciones.domain.Ruta;
import back.tpi.ms_GestionDeOperaciones.domain.SolicitudTraslado;
import back.tpi.ms_GestionDeOperaciones.domain.Tramo;
import back.tpi.ms_GestionDeOperaciones.dto.SolicitudTrasladoDTO;
import back.tpi.ms_GestionDeOperaciones.repository.RutaRepository;
import back.tpi.ms_GestionDeOperaciones.repository.SolicitudTrasladoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class RutaTentativaService {

    private final DistanciaService distanciaService;
    private final RutaRepository rutaRepository;
    private final SolicitudTrasladoRepository solicitudRepository;
    private final TarifaClient tarifaClient;

    /**
     * Consulta las rutas tentativas asociadas a una solicitud de traslado,
     * y calcula para cada una sus tramos con distancia, duración y costo estimado.
     * No asigna ninguna ruta — el usuario elige manualmente cuál usar.
     */
    public List<Ruta> consultarRutasTentativas(SolicitudTraslado solicitud) {
        List<Ruta> rutasTentativas = rutaRepository.findBySolicitudTrasladoId(solicitud.getId());

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
            }

            double costoEstimado = tarifaClient.calcularCostoEstimado(solicitud.getTarifaId(), distanciaTotal);
        }

        return rutasTentativas;
    }

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
