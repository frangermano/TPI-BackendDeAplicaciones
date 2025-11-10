package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import lombok.*;

/**
 * DTO simplificado con solo 3 filtros esenciales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltrosContenedorDTO {

    // Filtro 1: Por estado de la solicitud
    private EstadoSolicitud estado;

    // Filtro 2: Por cliente
    private Long clienteId;

    // Filtro 3: Solo mostrar atrasados
    private Boolean soloAtrasados;
}
