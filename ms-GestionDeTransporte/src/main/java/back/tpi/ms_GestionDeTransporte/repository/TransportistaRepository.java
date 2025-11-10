package back.tpi.ms_GestionDeTransporte.repository;

import back.tpi.ms_GestionDeTransporte.domain.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
    Optional<Transportista> findByEmail(String email);
    List<Transportista> findByDisponible(Boolean disponible);
    boolean existsByEmail(String email);
}