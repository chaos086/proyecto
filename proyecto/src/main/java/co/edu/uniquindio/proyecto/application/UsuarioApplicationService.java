package co.edu.uniquindio.proyecto.application;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueObject.IdentificacionUsuario;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.Rol;
import co.edu.uniquindio.proyecto.infrastructure.persistence.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioApplicationService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioApplicationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crearUsuario(String nombre, Rol rol) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        Usuario usuario = Usuario.crear(nombre, rol);
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuario(IdentificacionUsuario id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public void desactivarUsuario(IdentificacionUsuario id) {
        Usuario usuario = obtenerUsuario(id);
        usuario.desactivar();
        usuarioRepository.save(usuario);
    }

    public void activarUsuario(IdentificacionUsuario id) {
        Usuario usuario = obtenerUsuario(id);
        usuario.activar();
        usuarioRepository.save(usuario);
    }
}