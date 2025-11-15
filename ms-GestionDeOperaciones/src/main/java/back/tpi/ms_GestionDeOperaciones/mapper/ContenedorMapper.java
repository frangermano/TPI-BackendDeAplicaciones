package back.tpi.ms_GestionDeOperaciones.mapper;

import back.tpi.ms_GestionDeOperaciones.domain.Contenedor;
import back.tpi.ms_GestionDeOperaciones.dto.ContenedorDTO;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContenedorMapper {
    public ContenedorDTO toDTO(Contenedor contenedor) {
        return ContenedorDTO.builder()
                .contenedorId(contenedor.getId())
                .peso((contenedor.getPeso()))
                .volumen(contenedor.getVolumen())
                .build();
    }
}
