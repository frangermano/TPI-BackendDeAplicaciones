package back.tpi.ms_GestionDeOperaciones.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ruta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Integer cantidadTramos;

    @Column(nullable = false)
    Integer cantidadDepositos;

    // Relación con SolicitudTraslado (una ruta pertenece a una solicitud)
    @OneToOne
    @JoinColumn(name = "solicitud_traslado_id", nullable = false)
    SolicitudTraslado solicitudTraslado;

    // Relación con Tramos (una ruta tiene múltiples tramos)
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<Tramo> tramos = new ArrayList<>();

    // Metodo helper para agregar tramos
    public void agregarTramo(Tramo tramo) {
        tramos.add(tramo);
        tramo.setRuta(this);
        this.cantidadTramos = tramos.size();
    }

    // Metodo helper para calcular depositos
    public void calcularCantidadDepositos() {
        this.cantidadDepositos = (int) tramos.stream()
                .filter(t -> "DEPOSITO".equalsIgnoreCase(t.getTipoTramo()))
                .count();
    }
}