package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private int telefono;
}
