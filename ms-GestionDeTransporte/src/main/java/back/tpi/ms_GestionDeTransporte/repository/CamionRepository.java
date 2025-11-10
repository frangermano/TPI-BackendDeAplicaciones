package back.tpi.ms_GestionDeTransporte.repository;

import back.tpi.ms_GestionDeTransporte.domain.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CamionRepository extends JpaRepository<Camion, String> {
    List<Camion> findByDisponible(Boolean disponible);
    List<Camion> findByTransportistaId(Long transportistaId);

    @Query("SELECT c FROM Camion c JOIN FETCH c.transportista JOIN FETCH c.tipoCamion")
    List<Camion> findAllWithDetails();

    @Query("SELECT c FROM Camion c JOIN FETCH c.transportista JOIN FETCH c.tipoCamion WHERE c.patente = :patente")
    Camion findByPatenteWithDetails(String patente);
}
