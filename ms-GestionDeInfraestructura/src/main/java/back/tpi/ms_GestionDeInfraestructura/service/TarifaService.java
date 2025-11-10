package back.tpi.ms_GestionDeInfraestructura.service;

import back.tpi.ms_GestionDeInfraestructura.domain.Tarifa;
import back.tpi.ms_GestionDeInfraestructura.dto.TarifaDTO;
import back.tpi.ms_GestionDeInfraestructura.repository.TarifaRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TarifaService {

    private final TarifaRepository repository;

    /**
     * Crea una nueva tarifa a partir de un DTO
     * Convierte el DTO a entidad y la guarda
     */
    @Transactional
    public Tarifa crearTarifa(TarifaDTO tarifaDTO) {
        log.info("Creando nueva tarifa: {}", tarifaDTO.getNombre());

        // Convertir DTO a entidad
        Tarifa tarifa = Tarifa.builder()
                .nombre(tarifaDTO.getNombre())
                .patenteCamion(tarifaDTO.getPatenteCamion())
                .valorCombustibleLitro(tarifaDTO.getValorCombustibleLitro())
                .cargoGestionTrama(tarifaDTO.getCargoGestionTrama())
                .fechaVigencia(tarifaDTO.getFechaVigencia() != null ?
                        tarifaDTO.getFechaVigencia() : new Date())
                .idTipoCamion(tarifaDTO.getIdTipoCamion())
                .idDeposito(tarifaDTO.getIdDeposito())
                .build();

        Tarifa tarifaGuardada = repository.save(tarifa);
        log.info("Tarifa creada exitosamente con ID: {}", tarifaGuardada.getId());

        return tarifaGuardada;
    }

    @Transactional(readOnly = true)
    public List<Tarifa> obtenerTodas() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Tarifa> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeTarifa(Long id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Tarifa> obtenerPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    @Transactional(readOnly = true)
    public List<Tarifa> obtenerTarifasVigentes() {
        return repository.findByFechaVigencia(new Date());
    }

    @Transactional(readOnly = true)
    public List<Tarifa> obtenerPorTipoCamion(Long idTipoCamion) {
        return repository.findByIdTipoCamion(idTipoCamion);
    }

    @Transactional(readOnly = true)
    public List<Tarifa> obtenerPorDeposito(Long idDeposito) {
        return repository.findByIdDeposito(idDeposito);
    }

    /**
     * Calcula el costo total del traslado basado en la tarifa y la distancia
     * Fórmula: (distancia * consumo_por_km * valor_combustible_litro) + cargo_gestion
     */
    @Transactional(readOnly = true)
    public Double calcularCosto(Long tarifaId, Double distanciaKm) {
        Tarifa tarifa = repository.findById(tarifaId)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con ID: " + tarifaId));

        // Asumimos que el camión consume aproximadamente 0.3 litros por km
        double consumoPorKm = 0.3;
        double costoCombustible = distanciaKm * consumoPorKm * tarifa.getValorCombustibleLitro();
        double costoTotal = costoCombustible + tarifa.getCargoGestionTrama();

        log.info("Calculando costo para tarifa {}: distancia={}km, costo={}",
                tarifaId, distanciaKm, costoTotal);

        return costoTotal;
    }

    @Transactional
    public Tarifa actualizarTarifa(Long id, Tarifa tarifaActualizada) {
        Tarifa tarifa = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con ID: " + id));

        // Actualizar solo los campos permitidos
        if (tarifaActualizada.getNombre() != null) {
            tarifa.setNombre(tarifaActualizada.getNombre());
        }
        if (tarifaActualizada.getValorCombustibleLitro() != 0) {
            tarifa.setValorCombustibleLitro(tarifaActualizada.getValorCombustibleLitro());
        }
        if (tarifaActualizada.getCargoGestionTrama() != 0) {
            tarifa.setCargoGestionTrama(tarifaActualizada.getCargoGestionTrama());
        }
        if (tarifaActualizada.getFechaVigencia() != null) {
            tarifa.setFechaVigencia(tarifaActualizada.getFechaVigencia());
        }

        return repository.save(tarifa);
    }

    @Transactional
    public void eliminarTarifa(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Tarifa no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}