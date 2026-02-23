package co.edu.uniquindio.proyecto.domain.valueObject;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import java.util.UUID;

public record UsuarioReferencia(UUID value, String nombre) {
    public UsuarioReferencia {
        if (value == null) {
            throw new DomainException("UsuarioReferencia.id no puede ser null");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("UsuarioReferencia.nombre no puede estar vacío");
        }
    }

    public static UsuarioReferencia from(IdentificacionUsuario id, String nombre) {
        return new UsuarioReferencia(id.value(), nombre);
    }
}