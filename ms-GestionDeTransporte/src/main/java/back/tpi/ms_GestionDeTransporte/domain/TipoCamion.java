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

    @Column(nullable = false, unique = true)
    String nombre;

    @Column(nullable = false, name = "capacidad_volumen")
    Double capacidadVolumen;

    @Column(nullable = false, name = "capacidad_peso")
    Double capacidadPeso;

}
