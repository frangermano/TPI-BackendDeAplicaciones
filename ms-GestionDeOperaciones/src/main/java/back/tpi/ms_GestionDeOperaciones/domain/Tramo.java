package back.tpi.ms_GestionDeOperaciones.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "tramo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String origen;

    @Column(nullable = false)
    String destino;

    @Column(nullable = false, length = 50)
    String tipoTramo; // TRANSPORTE, DEPOSITO

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    EstadoTramo estado;

    @Column(nullable = false)
    Double costoAproximado;

    Double costoReal;

    LocalDateTime fechaHoraInicio;
    LocalDateTime fechaHoraFin;

    @Column(length = 20)
    String camionPatente; // Patente del camión (ej: "ABC123", "AB123CD")


    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    Ruta ruta;

    // Coordenadas opcionales
    Double coordOrigenLat;
    Double coordOrigenLng;
    Double coordDestinoLat;
    Double coordDestinoLng;

    Double distancia;
}
