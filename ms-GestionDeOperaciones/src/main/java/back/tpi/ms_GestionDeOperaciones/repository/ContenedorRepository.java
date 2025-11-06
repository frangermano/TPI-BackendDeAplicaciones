package back.tpi.ms_GestionDeOperaciones.repository;

import back.tpi.ms_GestionDeOperaciones.domain.Cliente;
import back.tpi.ms_GestionDeOperaciones.domain.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {

    // Buscar contenedores por cliente
    List<Contenedor> findByCliente(Cliente cliente);

    // Buscar contenedores por ID de cliente
    List<Contenedor> findByClienteId(Long clienteId);

    // Buscar por rango de peso
    List<Contenedor> findByPesoBetween(Double pesoMin, Double pesoMax);

    // Buscar por rango de volumen
    List<Contenedor> findByVolumenBetween(Double volumenMin, Double volumenMax);
}