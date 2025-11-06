package back.tpi.ms_GestionDeOperaciones.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "contenedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "peso", nullable = false)
    double peso;

    @Column(nullable = false)
    double volumen;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    Cliente cliente;
}
