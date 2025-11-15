package back.tpi.ms_GestionDeOperaciones.mapper;

import back.tpi.ms_GestionDeOperaciones.domain.Cliente;
import back.tpi.ms_GestionDeOperaciones.dto.ClienteDTO;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteMapper {

    public ClienteDTO toDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .clienteId(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .build();

    }
}
