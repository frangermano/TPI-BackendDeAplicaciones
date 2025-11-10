package back.tpi.ms_GestionDeOperaciones.repository;

import back.tpi.ms_GestionDeOperaciones.domain.Ruta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    // Buscar ruta por solicitud de traslado
    Optional<Ruta> findBySolicitudTrasladoId(Long solicitudTrasladoId);

    // Buscar todas las rutas asociadas a una solicitud
    @Query("SELECT r FROM Ruta r WHERE r.solicitudTraslado.id = :solicitudId")
    List<Ruta> findAllBySolicitudTrasladoId(@Param("solicitudId") Long solicitudId);


    // Verificar si existe ruta para una solicitud
    boolean existsBySolicitudTrasladoId(Long solicitudTrasladoId);

}
