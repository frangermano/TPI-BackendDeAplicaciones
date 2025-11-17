package back.tpi.ms_GestionDeTransporte.service;

import back.tpi.ms_GestionDeTransporte.domain.TipoCamion;
import back.tpi.ms_GestionDeTransporte.dto.TipoCamionRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.TipoCamionResponseDTO;
import back.tpi.ms_GestionDeTransporte.mapper.TipoCamionMapper;
import back.tpi.ms_GestionDeTransporte.repository.TipoCamionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoCamionService {

    private final TipoCamionRepository tipoCamionRepository;
    private final TipoCamionMapper tipoCamionMapper;

    @Transactional
    public TipoCamionResponseDTO crearTipoCamion(TipoCamionRequestDTO requestDTO) {
        // Validar que el nombre no exista
        if (tipoCamionRepository.existsByNombre(requestDTO.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de camion con el nombre: " + requestDTO.getNombre());
        }

        TipoCamion tipoCamion = tipoCamionMapper.toEntity(requestDTO);
        TipoCamion savedTipoCamion = tipoCamionRepository.save(tipoCamion);

        return tipoCamionMapper.toResponseDTO(savedTipoCamion);
    }

    @Transactional(readOnly = true)
    public TipoCamionResponseDTO obtenerTipoCamionPorId(Long id) {
        TipoCamion tipoCamion = tipoCamionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de camion no encontrado con ID: " + id));

        return tipoCamionMapper.toResponseDTO(tipoCamion);
    }

    @Transactional(readOnly = true)
    public List<TipoCamionResponseDTO> obtenerTodosTiposCamion() {
        return tipoCamionRepository.findAll().stream()
                .map(tipoCamionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TipoCamionResponseDTO actualizarTipoCamion(Long id, TipoCamionRequestDTO requestDTO) {
        TipoCamion tipoCamion = tipoCamionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de camion no encontrado con ID: " + id));

        // Validar nombre si cambio
        if (!tipoCamion.getNombre().equals(requestDTO.getNombre())
                && tipoCamionRepository.existsByNombre(requestDTO.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe un tipo de camion con el nombre: " + requestDTO.getNombre());
        }

        tipoCamion.setNombre(requestDTO.getNombre());
        tipoCamion.setCapacidadVolumen(requestDTO.getCapacidadVolumen());
        tipoCamion.setCapacidadPeso(requestDTO.getCapacidadPeso());

        TipoCamion updatedTipoCamion = tipoCamionRepository.save(tipoCamion);
        return tipoCamionMapper.toResponseDTO(updatedTipoCamion);
    }

    @Transactional
    public void eliminarTipoCamion(Long id) {
        if (!tipoCamionRepository.existsById(id)) {
            throw new IllegalArgumentException("Tipo de camion no encontrado con ID: " + id);
        }
        tipoCamionRepository.deleteById(id);
    }
}