package back.tpi.ms_GestionDeInfraestructura.repository;


import back.tpi.ms_GestionDeInfraestructura.domain.Tarifa;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    Optional<Tarifa> findByNombre(String nombre);

    List<Tarifa> findByFechaVigencia(Date fecha);

    List<Tarifa> findByIdTipoCamion(Long idTipoCamion);

    List<Tarifa> findByIdDeposito(Long idDeposito);
}