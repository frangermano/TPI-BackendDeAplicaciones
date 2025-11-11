package back.tpi.ms_GestionDeOperaciones.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud_traslado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SolicitudTraslado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    Cliente cliente;

    @OneToOne
    @JoinColumn(nullable = false)
    Contenedor contenedor;

    @Column(nullable = false)
    Long tarifaId;

    @Column(nullable = false)
    String direccionOrigen;

    Double coordOrigenLat;
    Double coordOrigenLng;

    @Column(nullable = false)
    String direccionDestino;

    Double coordDestinoLat;
    Double coordDestinoLng;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    EstadoSolicitud estado;

    Double costoEstimado;
    String tiempoEstimado; // en horas
    String tiempoReal;     // en horas
    Double costoFinal;

    @Column(nullable = false)
    LocalDateTime fechaSolicitud;

    LocalDateTime fechaInicio;
    LocalDateTime fechaFinalizacion;

    @OneToOne(mappedBy = "solicitudTraslado", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    Ruta ruta;
}
