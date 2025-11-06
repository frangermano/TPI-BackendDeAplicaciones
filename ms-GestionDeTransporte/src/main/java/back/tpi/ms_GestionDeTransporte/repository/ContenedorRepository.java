package back.tpi.ms_GestionDeTransporte.repository;

import back.tpi.ms_GestionDeTransporte.domain.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {

    Optional<Contenedor> findByNumeroSerie(String numeroSerie);

    List<Contenedor> findByEstado(String estado);

    List<Contenedor> findByTipo(String tipo);

    List<Contenedor> findByDepositoId(Long depositoId);
}