package back.tpi.ms_GestionDeInfraestructura.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Entity
@Table(name = "contendor_X_deposito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContenedorXDeposito {
    @Id
    long contenedor_id;

    @Id
    @ManyToOne
    Deposito deposito;
}
