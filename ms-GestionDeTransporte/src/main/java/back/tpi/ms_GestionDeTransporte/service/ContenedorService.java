package back.tpi.ms_GestionDeTransporte.service;

import back.tpi.ms_GestionDeTransporte.domain.Contenedor;
import back.tpi.ms_GestionDeTransporte.repository.ContenedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContenedorService {

    private final ContenedorRepository repository;

    @Transactional
    public Contenedor crearContenedor(Contenedor contenedor) {
        log.info("Creando nuevo contenedor con número de serie: {}", contenedor.getNumeroSerie());

        // Validar que no exista otro contenedor con el mismo número de serie
        if (contenedor.getNumeroSerie() != null &&
                repository.findByNumeroSerie(contenedor.getNumeroSerie()).isPresent()) {
            throw new RuntimeException("Ya existe un contenedor con el número de serie: " +
                    contenedor.getNumeroSerie());
        }

        return repository.save(contenedor);
    }

    @Transactional(readOnly = true)
    public List<Contenedor> obtenerTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Contenedor> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeContenedor(Long id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Contenedor> obtenerPorNumeroSerie(String numeroSerie) {
        return repository.findByNumeroSerie(numeroSerie);
    }

    @Transactional(readOnly = true)
    public List<Contenedor> obtenerPorEstado(String estado) {
        return repository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<Contenedor> obtenerPorTipo(String tipo) {
        return repository.findByTipo(tipo);
    }

    @Transactional(readOnly = true)
    public List<Contenedor> obtenerPorDeposito(Long depositoId) {
        return repository.findByDepositoId(depositoId);
    }

    @Transactional(readOnly = true)
    public List<Contenedor> obtenerDisponibles() {
        return repository.findByEstado("DISPONIBLE");
    }

    @Transactional
    public Contenedor actualizarEstado(Long id, String nuevoEstado) {
        Contenedor contenedor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con ID: " + id));

        contenedor.setEstado(nuevoEstado);
        log.info("Actualizando estado del contenedor {} a {}", id, nuevoEstado);

        return repository.save(contenedor);
    }

    @Transactional
    public Contenedor actualizarUbicacion(Long id, Long depositoId) {
        Contenedor contenedor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con ID: " + id));

        contenedor.setDepositoId(depositoId);
        log.info("Actualizando ubicación del contenedor {} al depósito {}", id, depositoId);

        return repository.save(contenedor);
    }

    @Transactional
    public Contenedor actualizarContenedor(Long id, Contenedor contenedorActualizado) {
        Contenedor contenedor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con ID: " + id));

        // Actualizar campos permitidos
        if (contenedorActualizado.getNumeroSerie() != null) {
            contenedor.setNumeroSerie(contenedorActualizado.getNumeroSerie());
        }
        if (contenedorActualizado.getTipo() != null) {
            contenedor.setTipo(contenedorActualizado.getTipo());
        }
        if (contenedorActualizado.getEstado() != null) {
            contenedor.setEstado(contenedorActualizado.getEstado());
        }
        if (contenedorActualizado.getCapacidad() != null) {
            contenedor.setCapacidad(contenedorActualizado.getCapacidad());
        }
        if (contenedorActualizado.getDepositoId() != null) {
            contenedor.setDepositoId(contenedorActualizado.getDepositoId());
        }

        return repository.save(contenedor);
    }

    @Transactional
    public void eliminarContenedor(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Contenedor no encontrado con ID: " + id);
        }

        log.info("Eliminando contenedor con ID: {}", id);
        repository.deleteById(id);
    }
}
