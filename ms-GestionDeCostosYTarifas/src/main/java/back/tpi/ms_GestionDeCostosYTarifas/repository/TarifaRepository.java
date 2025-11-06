package back.tpi.ms_GestionDeCostosYTarifas.repository;

import back.tpi.ms_GestionDeCostosYTarifas.domain.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    Optional<Tarifa> findByNombre(String nombre);

    List<Tarifa> findByFechaVigenciaAfter(Date fecha);

    List<Tarifa> findByIdTipoCamion(Long idTipoCamion);

    List<Tarifa> findByIdDeposito(Long idDeposito);
}
