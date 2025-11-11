package back.tpi.ms_GestionDeOperaciones.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositoDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private Double coordenadas; // Este campo parece almacenar lat,lng juntos
    private Double costoEstadia;

    // Campos adicionales para parsear coordenadas
    private Double latitud;
    private Double longitud;

    // Campos calculados (para la búsqueda de rutas)
    private Double distanciaDesdeOrigen; // en km
    private Double distanciaHastaDestino; // en km
    private Double distanciaTotal; // suma de las dos anteriores
    private Boolean enRuta; // si está cerca de la ruta directa
    private Double desviacionKm; // cuánto te desvías de la ruta directa

    /**
     * Parsea el campo coordenadas (formato esperado: "lat,lng" o solo un double)
     * y establece latitud y longitud
     */
    public void parsearCoordenadas() {
        if (this.coordenadas != null) {
            // Si coordenadas es un String en formato "lat,lng"
            // necesitarás ajustar según tu formato real
            // Por ahora asumo que almacenas solo latitud o necesitas ajustar
        }
    }

    /**
     * Constructor para crear desde coordenadas separadas
     */
    public static DepositoDTO conCoordenadas(Long id, String nombre, String direccion,
                                             Double latitud, Double longitud, Double costoEstadia) {
        DepositoDTO dto = new DepositoDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setDireccion(direccion);
        dto.setLatitud(latitud);
        dto.setLongitud(longitud);
        dto.setCostoEstadia(costoEstadia);
        return dto;
    }
}