package co.edu.uniquindio.proyecto.infrastructure.persistence;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueObject.IdentificacionUsuario;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(IdentificacionUsuario id);
    List<Usuario> findAll();
    void delete(Usuario usuario);
    boolean existsById(IdentificacionUsuario id);
}