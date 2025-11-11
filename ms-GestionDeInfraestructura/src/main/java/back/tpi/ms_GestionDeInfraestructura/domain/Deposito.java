package back.tpi.ms_GestionDeInfraestructura.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Entity
@Table(name = "deposito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Deposito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String nombre;

    @Column(nullable = false)
    String direccion;

    @Column(nullable = false)
    Double latitud;

    @Column(nullable = false)
    Double longitud;

    @Column(nullable = false)
    double costoEstadia;
}
