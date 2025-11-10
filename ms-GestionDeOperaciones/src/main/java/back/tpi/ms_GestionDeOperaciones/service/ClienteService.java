package back.tpi.ms_GestionDeOperaciones.service;

import back.tpi.ms_GestionDeOperaciones.domain.Cliente;
import back.tpi.ms_GestionDeOperaciones.dto.ClienteDTO;
import back.tpi.ms_GestionDeOperaciones.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository repository;

    /**
     * Obtiene o registra un cliente
     * Si existe por email, lo retorna; si no, lo crea
     */
    @Transactional
    public Cliente obtenerORegistrarCliente(ClienteDTO clienteDTO) {
        // Buscar por email
        Optional<Cliente> clienteExistente = repository.findByEmail(clienteDTO.getEmail());

        if (clienteExistente.isPresent()) {
            log.info("Cliente encontrado con email: {}", clienteDTO.getEmail());
            return clienteExistente.get();
        }

        // Si no existe, crearlo
        log.info("Registrando nuevo cliente con email: {}", clienteDTO.getEmail());
        Cliente nuevoCliente = Cliente.builder()
                .nombre(clienteDTO.getNombre())
                .apellido(clienteDTO.getApellido())
                .email(clienteDTO.getEmail())
                .telefono(clienteDTO.getTelefono())
                .build();

        return repository.save(nuevoCliente);
    }

    @Transactional
    public Cliente crearCliente(ClienteDTO clienteDTO) {
        if (repository.existsByEmail(clienteDTO.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con email: " + clienteDTO.getEmail());
        }

        Cliente cliente = Cliente.builder()
                .nombre(clienteDTO.getNombre())
                .apellido(clienteDTO.getApellido())
                .email(clienteDTO.getEmail())
                .telefono(clienteDTO.getTelefono())
                .build();

        return repository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> obtenerTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    public Cliente actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = obtenerPorId(id);

        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefono(clienteDTO.getTelefono());

        return repository.save(cliente);
    }

    @Transactional
    public void eliminarCliente(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}
