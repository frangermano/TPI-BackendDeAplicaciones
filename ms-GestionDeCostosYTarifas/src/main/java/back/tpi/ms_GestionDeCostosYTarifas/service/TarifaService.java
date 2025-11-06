package back.tpi.ms_GestionDeCostosYTarifas.service;

import back.tpi.ms_GestionDeCostosYTarifas.domain.Tarifa;
import back.tpi.ms_GestionDeCostosYTarifas.repository.TarifaRepository;
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


    @Transactional
    public Tarifa crearTarifa(Tarifa tarifa) {
        log.info("Creando nueva tarifa: {}", tarifa.getNombre());
        return repository.save(tarifa);
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
        return repository.findByFechaVigenciaAfter(new Date());
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
     * Fórmula: (distancia * valor_combustible_por_km) + cargo_gestion
     */
    @Transactional(readOnly = true)
    public Double calcularCosto(Long tarifaId, Double distanciaKm) {
        Tarifa tarifa = repository.findById(tarifaId)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con ID: " + tarifaId));

        // Asumimos que el camión consume aproximadamente 0.3 litros por km
        double consumoPorKm = 0.3;
        double costoCombustible = distanciaKm * consumoPorKm * tarifa.getValor_combustible_litro();
        double costoTotal = costoCombustible + tarifa.getCargo_gestion_trama();

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
        if (tarifaActualizada.getValor_combustible_litro() != 0) {
            tarifa.setValor_combustible_litro(tarifaActualizada.getValor_combustible_litro());
        }
        if (tarifaActualizada.getCargo_gestion_trama() != 0) {
            tarifa.setCargo_gestion_trama(tarifaActualizada.getCargo_gestion_trama());
        }
        if (tarifaActualizada.getFecha_vigencia() != null) {
            tarifa.setFecha_vigencia(tarifaActualizada.getFecha_vigencia());
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

