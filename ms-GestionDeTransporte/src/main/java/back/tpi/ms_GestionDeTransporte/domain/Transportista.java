package back.tpi.ms_GestionDeTransporte.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Entity
@Table(name = "transportista")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transportista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String nombre;

    @Column(nullable = false)
    int telefono;

    @Column(nullable = false)
    String email;

    @Column
    boolean disponible;
}
