package back.tpi.ms_GestionDeOperaciones.repository;

import back.tpi.ms_GestionDeOperaciones.domain.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    // Buscar ruta por solicitud de traslado
    List<Ruta> findBySolicitudTrasladoId(Long solicitudTrasladoId);

    // Verificar si existe ruta para una solicitud
    boolean existsBySolicitudTrasladoId(Long solicitudTrasladoId);

}
