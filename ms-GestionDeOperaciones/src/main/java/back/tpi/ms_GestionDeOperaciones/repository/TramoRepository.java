package back.tpi.ms_GestionDeOperaciones.repository;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoTramo;
import back.tpi.ms_GestionDeOperaciones.domain.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {

    // Buscar tramos por ruta
    List<Tramo> findByRutaId(Long rutaId);

    // Buscar tramos por estado
    List<Tramo> findByEstado(EstadoTramo estado);

    // Buscar tramos por camión
    List<Tramo> findByCamionId(Long camionId);

    // Buscar tramos de una ruta por estado
    List<Tramo> findByRutaIdAndEstado(Long rutaId, EstadoTramo estado);
}
