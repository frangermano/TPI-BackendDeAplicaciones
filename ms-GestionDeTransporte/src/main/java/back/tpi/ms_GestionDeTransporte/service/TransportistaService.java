package back.tpi.ms_GestionDeTransporte.service;

import back.tpi.ms_GestionDeTransporte.domain.Transportista;
import back.tpi.ms_GestionDeTransporte.dto.TransportistaRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.TransportistaResponseDTO;
import back.tpi.ms_GestionDeTransporte.mapper.TransportistaMapper;
import back.tpi.ms_GestionDeTransporte.repository.TransportistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportistaService {

    private final TransportistaRepository transportistaRepository;
    private final TransportistaMapper transportistaMapper;

    @Transactional
    public TransportistaResponseDTO registrarTransportista(TransportistaRequestDTO requestDTO) {
        // Validar que el email no exista
        if (transportistaRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un transportista con el email: " + requestDTO.getEmail());
        }

        Transportista transportista = transportistaMapper.toEntity(requestDTO);
        Transportista savedTransportista = transportistaRepository.save(transportista);

        return transportistaMapper.toResponseDTO(savedTransportista);
    }

    @Transactional(readOnly = true)
    public TransportistaResponseDTO obtenerTransportistaPorId(Long id) {
        Transportista transportista = transportistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transportista no encontrado con ID: " + id));

        return transportistaMapper.toResponseDTO(transportista);
    }

    @Transactional(readOnly = true)
    public List<TransportistaResponseDTO> obtenerTodosLosTransportistas() {
        return transportistaRepository.findAll().stream()
                .map(transportistaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransportistaResponseDTO> obtenerTransportistasDisponibles() {
        return transportistaRepository.findByDisponible(true).stream()
                .map(transportistaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransportistaResponseDTO actualizarTransportista(Long id, TransportistaRequestDTO requestDTO) {
        Transportista transportista = transportistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transportista no encontrado con ID: " + id));

        // Validar email si cambio
        if (!transportista.getEmail().equals(requestDTO.getEmail())
                && transportistaRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un transportista con el email: " + requestDTO.getEmail());
        }

        transportista.setNombre(requestDTO.getNombre());
        transportista.setTelefono(requestDTO.getTelefono());
        transportista.setEmail(requestDTO.getEmail());
        transportista.setDisponible(requestDTO.getDisponible());

        Transportista updatedTransportista = transportistaRepository.save(transportista);
        return transportistaMapper.toResponseDTO(updatedTransportista);
    }

    @Transactional
    public void eliminarTransportista(Long id) {
        if (!transportistaRepository.existsById(id)) {
            throw new IllegalArgumentException("Transportista no encontrado con ID: " + id);
        }
        transportistaRepository.deleteById(id);
    }
}