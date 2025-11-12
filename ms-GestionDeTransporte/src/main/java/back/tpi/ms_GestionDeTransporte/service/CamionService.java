package back.tpi.ms_GestionDeTransporte.service;

import back.tpi.ms_GestionDeTransporte.domain.Camion;
import back.tpi.ms_GestionDeTransporte.domain.TipoCamion;
import back.tpi.ms_GestionDeTransporte.domain.Transportista;
import back.tpi.ms_GestionDeTransporte.dto.CamionRequestDTO;
import back.tpi.ms_GestionDeTransporte.dto.CamionResponseDTO;
import back.tpi.ms_GestionDeTransporte.exception.DuplicateResourceException;
import back.tpi.ms_GestionDeTransporte.exception.ResourceNotFoundException;
import back.tpi.ms_GestionDeTransporte.mapper.CamionMapper;
import back.tpi.ms_GestionDeTransporte.repository.CamionRepository;
import back.tpi.ms_GestionDeTransporte.repository.TipoCamionRepository;
import back.tpi.ms_GestionDeTransporte.repository.TransportistaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CamionService {

    private final CamionRepository camionRepository;
    private final TransportistaRepository transportistaRepository;
    private final TipoCamionRepository tipoCamionRepository;
    private final CamionMapper camionMapper;

    @Transactional
    public CamionResponseDTO registrarCamion(CamionRequestDTO requestDTO) {
        // Validar que la patente no exista
        if (camionRepository.existsById(requestDTO.getPatente())) {
            throw new DuplicateResourceException("Ya existe un camión con la patente: " + requestDTO.getPatente());
        }

        // Validar que el transportista exista
        Transportista transportista = transportistaRepository.findById(requestDTO.getIdTransportista())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transportista no encontrado con ID: " + requestDTO.getIdTransportista()));

        // Validar que el tipo de camión exista
        TipoCamion tipoCamion = tipoCamionRepository.findById(requestDTO.getIdTipoCamion())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de camión no encontrado con ID: " + requestDTO.getIdTipoCamion()));

        Camion camion = Camion.builder()
                .patente(requestDTO.getPatente().toUpperCase())
                .costoCombustible(requestDTO.getCostoCombustible())
                .costoKm(requestDTO.getCostoKm())
                .disponible(requestDTO.getDisponible() != null ? requestDTO.getDisponible() : true)
                .transportista(transportista)
                .tipoCamion(tipoCamion)
                .build();

        Camion savedCamion = camionRepository.save(camion);

        // Recargar con relaciones para el DTO
        savedCamion = camionRepository.findByPatenteWithDetails(savedCamion.getPatente());

        return camionMapper.toResponseDTO(savedCamion);
    }

    @Transactional(readOnly = true)
    public CamionResponseDTO obtenerCamionPorPatente(String patente) {
        Camion camion = camionRepository.findByPatenteWithDetails(patente.toUpperCase());

        if (camion == null) {
            throw new ResourceNotFoundException("Camión no encontrado con patente: " + patente);
        }

        return camionMapper.toResponseDTO(camion);
    }

    @Transactional(readOnly = true)
    public List<CamionResponseDTO> obtenerTodosLosCamiones() {
        return camionRepository.findAllWithDetails().stream()
                .map(camionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CamionResponseDTO> obtenerCamionesDisponibles() {
        return camionRepository.findByDisponible(true).stream()
                .map(camionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CamionResponseDTO> obtenerCamionesPorTransportista(Long transportistaId) {
        return camionRepository.findByTransportistaId(transportistaId).stream()
                .map(camionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza la disponibilidad de un camión
     */
    @Transactional
    public void actualizarDisponibilidad(String patente, Boolean disponible) {
        log.info("Actualizando disponibilidad del camión {} a: {}", patente, disponible);

        Camion camion = camionRepository.findById(patente.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Camión no encontrado con patente: " + patente));

        camion.setDisponible(disponible);
        camionRepository.save(camion);

        log.info("✅ Disponibilidad del camión {} actualizada a: {}", patente, disponible);
    }

    @Transactional
    public CamionResponseDTO actualizarCamion(String patente, CamionRequestDTO requestDTO) {
        Camion camion = camionRepository.findById(patente.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Camión no encontrado con patente: " + patente));

        // Validar transportista si cambió
        if (!camion.getTransportista().getId().equals(requestDTO.getIdTransportista())) {
            Transportista transportista = transportistaRepository.findById(requestDTO.getIdTransportista())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Transportista no encontrado con ID: " + requestDTO.getIdTransportista()));
            camion.setTransportista(transportista);
        }

        // Validar tipo de camión si cambió
        if (!camion.getTipoCamion().getId().equals(requestDTO.getIdTipoCamion())) {
            TipoCamion tipoCamion = tipoCamionRepository.findById(requestDTO.getIdTipoCamion())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de camión no encontrado con ID: " + requestDTO.getIdTipoCamion()));
            camion.setTipoCamion(tipoCamion);
        }

        camion.setCostoCombustible(requestDTO.getCostoCombustible());
        camion.setCostoKm(requestDTO.getCostoKm());
        camion.setDisponible(requestDTO.getDisponible());

        Camion updatedCamion = camionRepository.save(camion);
        updatedCamion = camionRepository.findByPatenteWithDetails(updatedCamion.getPatente());

        return camionMapper.toResponseDTO(updatedCamion);
    }

    @Transactional
    public void eliminarCamion(String patente) {
        if (!camionRepository.existsById(patente.toUpperCase())) {
            throw new ResourceNotFoundException("Camión no encontrado con patente: " + patente);
        }
        camionRepository.deleteById(patente.toUpperCase());
    }
}