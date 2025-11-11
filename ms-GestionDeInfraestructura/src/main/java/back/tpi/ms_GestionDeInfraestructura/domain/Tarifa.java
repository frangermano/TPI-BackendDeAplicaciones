package back.tpi.ms_GestionDeInfraestructura.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "tarifas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String nombre;

    // Referencia al camión en otro microservicio
    @Column(name = "patente_camion")
    String patenteCamion;

    @Column(nullable = false)
    @JsonProperty("ValorCombustibleLitro")
    double valorCombustibleLitro;

    @Column(nullable = false)
    double cargoGestionTrama;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    Date fechaVigencia;

    // IDs de referencia a otros microservicios
    @Column(name = "id_tipo_camion")
    Long idTipoCamion;

    @Column(name = "id_deposito")
    Long idDeposito;
}
