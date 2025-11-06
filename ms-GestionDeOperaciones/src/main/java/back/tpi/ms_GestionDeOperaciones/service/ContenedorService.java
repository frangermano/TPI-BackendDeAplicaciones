package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.domain.Cliente;
import back.tpi.ms_GestionDeOperaciones.domain.Contenedor;
import back.tpi.ms_GestionDeOperaciones.dto.ContenedorDTO;
import back.tpi.ms_GestionDeOperaciones.repository.ContenedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContenedorService {

    private final ContenedorRepository repository;

    /**
     * Crea un nuevo contenedor asociado a un cliente
     */
    @Transactional
    public Contenedor crearContenedor(ContenedorDTO contenedorDTO, Cliente cliente) {
        log.info("Creando contenedor para cliente ID: {}", cliente.getId());

        Contenedor contenedor = Contenedor.builder()
                .peso(contenedorDTO.getPeso())
                .volumen(contenedorDTO.getVolumen())
                .cliente(cliente)
                .build();

        return repository.save(contenedor);
    }

    @Transactional(readOnly = true)
    public List<Contenedor> obtenerTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Contenedor obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Contenedor> obtenerPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    @Transactional
    public Contenedor actualizarContenedor(Long id, ContenedorDTO contenedorDTO) {
        Contenedor contenedor = obtenerPorId(id);
        contenedor.setPeso(contenedorDTO.getPeso());
        contenedor.setVolumen(contenedorDTO.getVolumen());
        return repository.save(contenedor);
    }

    @Transactional
    public void eliminarContenedor(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Contenedor no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}