package co.edu.uniquindio.proyecto.infrastructure.api;

import co.edu.uniquindio.proyecto.application.UsuarioApplicationService;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueObject.IdentificacionUsuario;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.Rol;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller para la gestión de usuarios.
 * Expone los endpoints de la API para operaciones CRUD de usuarios.
 * 
 * Endpoints disponibles:
 * - POST /api/usuarios - Crear usuario
 * - GET /api/usuarios - Listar todos los usuarios
 * - GET /api/usuarios/{id} - Obtener usuario por ID
 * - PUT /api/usuarios/{id}/activar - Activar usuario
 * - PUT /api/usuarios/{id}/desactivar - Desactivar usuario
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioApplicationService usuarioService;

    public UsuarioController(UsuarioApplicationService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * @param payload Mapa con los datos del usuario (nombre, rol)
     * @return Usuario creado
     */
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Map<String, Object> payload) {
        String nombre = (String) payload.get("nombre");
        String rolStr = (String) payload.get("rol");
        Rol rol = Rol.valueOf(rolStr.toUpperCase());

        Usuario usuario = usuarioService.crearUsuario(nombre, rol);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Lista todos los usuarios registrados en el sistema.
     * @return Lista de usuarios
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    /**
     * Activa un usuario existente.
     * @param id UUID del usuario a activar
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<Void> activarUsuario(@PathVariable UUID id) {
        IdentificacionUsuario identificacion = new IdentificacionUsuario(id);
        usuarioService.activarUsuario(identificacion);
        return ResponseEntity.ok().build();
    }

    /**
     * Desactiva un usuario existente.
     * @param id UUID del usuario a desactivar
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable UUID id) {
        IdentificacionUsuario identificacion = new IdentificacionUsuario(id);
        usuarioService.desactivarUsuario(identificacion);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene un usuario por su identificador.
     * @param id UUID del usuario
     * @return Usuario encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable UUID id) {
        IdentificacionUsuario identificacion = new IdentificacionUsuario(id);
        Usuario usuario = usuarioService.obtenerUsuario(identificacion);
        return ResponseEntity.ok(usuario);
    }
}
