package back.tpi.ms_GestionDeTransporte.repository;

import back.tpi.ms_GestionDeTransporte.domain.TipoCamion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoCamionRepository extends JpaRepository<TipoCamion, Long> {
    Optional<TipoCamion> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}