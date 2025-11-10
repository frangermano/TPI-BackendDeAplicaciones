package back.tpi.ms_GestionDeTransporte.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "camion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Camion {
    @Id
    String patente;

    @Column(nullable = false)
    Double costoCombustible;

    @Column(nullable = false)
    Double costoKm;

    @Column
    Boolean disponible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista", nullable = false)
    Transportista transportista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_camion", nullable = false)
    TipoCamion tipoCamion;
}
