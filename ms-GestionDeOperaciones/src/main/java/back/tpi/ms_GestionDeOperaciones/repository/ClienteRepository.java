package back.tpi.ms_GestionDeOperaciones.repository;

import back.tpi.ms_GestionDeOperaciones.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar cliente por email (único)
    Optional<Cliente> findByEmail(String email);

    // Verificar si existe por email
    boolean existsByEmail(String email);
}
