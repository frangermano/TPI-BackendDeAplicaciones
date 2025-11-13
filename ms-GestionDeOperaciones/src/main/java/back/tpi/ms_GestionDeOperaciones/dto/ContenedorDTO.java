package back.tpi.ms_GestionDeOperaciones.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContenedorDTO {
    private Long id; // Será null al crear
    private double peso;
    private double volumen;
    private ClienteDTO cliente;
    //private String identificacionUnica; // Número de serie, código QR, etc.
    //private String tipo; // ESTANDAR, REFRIGERADO, etc.
    //private Double capacidad;
    //private String unidadMedida; // m3, kg, etc.
    //private String estado; // DISPONIBLE, EN_USO, etc.
}
