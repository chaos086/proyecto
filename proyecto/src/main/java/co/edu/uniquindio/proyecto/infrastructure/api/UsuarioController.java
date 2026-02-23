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

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioApplicationService usuarioService;

    public UsuarioController(UsuarioApplicationService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Map<String, Object> payload) {
        String nombre = (String) payload.get("nombre");
        String rolStr = (String) payload.get("rol");
        Rol rol = Rol.valueOf(rolStr.toUpperCase());

        Usuario usuario = usuarioService.crearUsuario(nombre, rol);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Void> activarUsuario(@PathVariable UUID id) {
        IdentificacionUsuario identificacion = new IdentificacionUsuario(id);
        usuarioService.activarUsuario(identificacion);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable UUID id) {
        IdentificacionUsuario identificacion = new IdentificacionUsuario(id);
        usuarioService.desactivarUsuario(identificacion);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable UUID id) {
        IdentificacionUsuario identificacion = new IdentificacionUsuario(id);
        Usuario usuario = usuarioService.obtenerUsuario(identificacion);
        return ResponseEntity.ok(usuario);
    }
}