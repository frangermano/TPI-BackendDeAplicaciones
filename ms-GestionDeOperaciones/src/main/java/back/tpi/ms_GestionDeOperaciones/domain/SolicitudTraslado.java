package back.tpi.ms_GestionDeOperaciones.domain;

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

    @Column(unique = true, nullable = false)
    Integer numero;

    // ====== REFERENCIA A CLIENTE (LOCAL - MISMO MICROSERVICIO) ======
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    Cliente cliente;  // ✅ Relación JPA normal


    @Column(nullable = false)
    Contenedor contenedor;

    // ====== IDs DE REFERENCIA A OTROS MICROSERVICIOS ======
    @Column(nullable = false)
    Long tarifaId;      // ID de la tarifa (en ms-tarifas)

    // ====== DATOS DE LA SOLICITUD ======
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
    Double tiempoEstimado; // en horas
    Double tiempoReal;     // en horas
    Double costoFinal;

    @Column(nullable = false)
    LocalDateTime fechaSolicitud;

    LocalDateTime fechaInicio;
    LocalDateTime fechaFinalizacion;

    @OneToOne(mappedBy = "solicitudTraslado", cascade = CascadeType.ALL, orphanRemoval = true)
    Ruta ruta;
}
