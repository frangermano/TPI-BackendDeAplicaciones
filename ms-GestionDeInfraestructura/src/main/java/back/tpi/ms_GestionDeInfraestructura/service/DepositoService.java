package back.tpi.ms_GestionDeInfraestructura.service;

import back.tpi.ms_GestionDeInfraestructura.domain.Deposito;
import back.tpi.ms_GestionDeInfraestructura.dto.DepositoDTO;
import back.tpi.ms_GestionDeInfraestructura.repository.DepositoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositoService {

    private final DepositoRepository depositoRepository;

    @Transactional(readOnly = true)
    public List<DepositoDTO> obtenerTodos() {
        return depositoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DepositoDTO obtenerPorId(Long id) {
        Deposito deposito = depositoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado con ID: " + id));
        return convertirADTO(deposito);
    }

    @Transactional(readOnly = true)
    public boolean existe(Long id) {
        return depositoRepository.existsById(id);
    }

    /**
     * Retorna TODOS los depósitos con sus coordenadas.
     * El cálculo de distancias se hace en el servicio consumidor.
     */
    @Transactional(readOnly = true)
    public List<DepositoDTO> encontrarDepositosEnRuta(
            Double latOrigen, Double lngOrigen,
            Double latDestino, Double lngDestino,
            Integer cantidad) {

        log.info("Retornando todos los depósitos disponibles para cálculo en cliente");

        // Simplemente retornar todos los depósitos
        // El filtrado y cálculo de distancias lo hará RutaTentativaService
        return depositoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DepositoDTO> encontrarDepositosCercanos(Double lat, Double lng, Double radioKm) {
        log.info("Retornando todos los depósitos disponibles");

        // Retornar todos, el filtrado se hace en el cliente
        return depositoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DepositoDTO crear(DepositoDTO depositoDTO) {
        Deposito deposito = Deposito.builder()
                .nombre(depositoDTO.getNombre())
                .direccion(depositoDTO.getDireccion())
                .latitud(depositoDTO.getLatitud())
                .longitud(depositoDTO.getLongitud())
                .costoEstadia(depositoDTO.getCostoEstadia())
                .build();

        Deposito depositoGuardado = depositoRepository.save(deposito);
        return convertirADTO(depositoGuardado);
    }

    @Transactional
    public DepositoDTO actualizar(Long id, DepositoDTO depositoDTO) {
        Deposito deposito = depositoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Depósito no encontrado con ID: " + id));

        // Actualizar los campos del depósito
        deposito.setNombre(depositoDTO.getNombre());
        deposito.setDireccion(depositoDTO.getDireccion());
        deposito.setLatitud(depositoDTO.getLatitud());
        deposito.setLongitud(depositoDTO.getLongitud());
        deposito.setCostoEstadia(depositoDTO.getCostoEstadia());

        Deposito depositoActualizado = depositoRepository.save(deposito);
        log.info("Depósito actualizado con ID: {}", id);

        return convertirADTO(depositoActualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!depositoRepository.existsById(id)) {
            throw new RuntimeException("Depósito no encontrado con ID: " + id);
        }

        depositoRepository.deleteById(id);
        log.info("Depósito eliminado con ID: {}", id);
    }

    private DepositoDTO convertirADTO(Deposito deposito) {
        return DepositoDTO.builder()
                .id(deposito.getId())
                .nombre(deposito.getNombre())
                .direccion(deposito.getDireccion())
                .latitud(deposito.getLatitud())
                .longitud(deposito.getLongitud())
                .costoEstadia(deposito.getCostoEstadia())
                .build();
    }
}