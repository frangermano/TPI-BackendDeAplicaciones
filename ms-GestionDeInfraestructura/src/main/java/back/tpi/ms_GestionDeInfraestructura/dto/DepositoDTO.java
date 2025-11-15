package back.tpi.ms_GestionDeInfraestructura.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "DepositoDTO",
        description = "Representa un depósito disponible para estacionamiento o descanso en la ruta. Incluye ubicación, costos y datos derivados para cálculos de proximidad."
)
public class DepositoDTO {

    @Schema(description = "Identificador único del depósito", example = "12")
    private Long id;

    @Schema(description = "Nombre del depósito", example = "Depósito Central Córdoba")
    private String nombre;

    @Schema(description = "Dirección física del depósito", example = "Av. Circunvalación 1234, Córdoba Capital")
    private String direccion;

    @Schema(
            description = "Coordenadas almacenadas en un único campo. Puede representar latitud, o un formato 'lat,lng' según la implementación del sistema.",
            example = "-31.4201"
    )
    private Double coordenadas;

    @Schema(description = "Costo de estadía por hora en el depósito", example = "1500.50")
    private Double costoEstadia;

    // ---- Coordenadas parseadas ----
    @Schema(description = "Latitud del depósito", example = "-31.4201")
    private Double latitud;

    @Schema(description = "Longitud del depósito", example = "-64.1888")
    private Double longitud;

    // ---- Campos calculados para ruteo ----
    @Schema(description = "Distancia desde el origen del viaje hasta este depósito (km)", example = "5.73")
    private Double distanciaDesdeOrigen;

    @Schema(description = "Distancia desde este depósito hasta el destino final (km)", example = "12.40")
    private Double distanciaHastaDestino;

    @Schema(description = "Distancia total combinada desde origen y hasta destino (km)", example = "18.13")
    private Double distanciaTotal;

    @Schema(description = "Indica si este depósito está dentro de la ruta directa esperada", example = "true")
    private Boolean enRuta;

    @Schema(description = "Desviación estimada en km respecto a la ruta principal", example = "1.80")
    private Double desviacionKm;

    /**
     * Parsea el campo coordenadas (formato esperado: "lat,lng" o solo un double)
     * y establece latitud y longitud.
     */
    public void parsearCoordenadas() {
        if (this.coordenadas != null) {
            // Lógica pendiente según formato real
        }
    }

    /**
     * Constructor para crear desde coordenadas separadas.
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
