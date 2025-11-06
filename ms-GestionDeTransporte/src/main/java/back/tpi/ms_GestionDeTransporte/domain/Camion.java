package back.tpi.ms_GestionDeTransporte.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    double costo_combustible;

    @Column(nullable = false)
    double costo_km;

    @Column
    boolean disponible;

    @Column(nullable = false)
    Transportista id_transportista;

    @Column(nullable = false)
    TipoCamion id_tipo_camion;
}
