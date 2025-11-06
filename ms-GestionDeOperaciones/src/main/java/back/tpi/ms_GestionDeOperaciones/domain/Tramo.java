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
    String tipoTramo; // TRANSPORTE, DEPOSITO, etc.

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    EstadoTramo estado; // PENDIENTE, EN_CURSO, COMPLETADO, CANCELADO

    @Column(nullable = false)
    Double costoAproximado;

    Double costoReal;

    LocalDateTime fechaHoraInicio;
    LocalDateTime fechaHoraFin;

    // Referencia al camión (ID en ms-GestionDeFlota)
    Long camionId;

    // Relación con Ruta (muchos tramos pertenecen a una ruta)
    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    Ruta ruta;

    // Coordenadas opcionales para tracking
    Double coordOrigenLat;
    Double coordOrigenLng;
    Double coordDestinoLat;
    Double coordDestinoLng;
}
