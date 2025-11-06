package back.tpi.ms_GestionDeTransporte.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tipo_camion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TipoCamion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String nombre;

    @Column(nullable = false)
    double capacidad_volumen;

    @Column(nullable = false)
    double capacidad_peso;

}
