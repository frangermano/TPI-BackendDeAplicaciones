package back.tpi.ms_GestionDeOperaciones.repository;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import back.tpi.ms_GestionDeOperaciones.domain.SolicitudTraslado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudTrasladoRepository extends JpaRepository<SolicitudTraslado, Long> {

    // Buscar por número de solicitud
    Optional<SolicitudTraslado> findByNumero(Integer numero);

    // Buscar por cliente
    List<SolicitudTraslado> findByClienteId(Long clienteId);

    // Buscar por contenedor
    List<SolicitudTraslado> findByContenedorId(Long contenedorId);

    // Buscar por estado
    List<SolicitudTraslado> findByEstado(EstadoSolicitud estado);

    // Buscar por tarifa
    List<SolicitudTraslado> findByTarifaId(Long tarifaId);

    /*
    // Buscar solicitudes entre fechas
    @Query("SELECT s FROM SolicitudTraslado s WHERE s.fechaSolicitud BETWEEN :inicio AND :fin")
    List<SolicitudTraslado> findByFechaSolicitudBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // Buscar solicitudes pendientes de un cliente
    List<SolicitudTraslado> findByClienteIdAndEstado(Long clienteId, EstadoSolicitud estado);

    // Contar solicitudes por estado
    Long countByEstado(EstadoSolicitud estado);

    // Verificar si existe solicitud activa para un contenedor
    @Query("SELECT COUNT(s) > 0 FROM SolicitudTraslado s " +
            "WHERE s.contenedorId = :contenedorId " +
            "AND s.estado IN ('PENDIENTE', 'APROBADA', 'EN_PROCESO')")
    boolean existeSolicitudActivaParaContenedor(@Param("contenedorId") Long contenedorId);

     */
}

