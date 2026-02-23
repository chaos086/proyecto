package co.edu.uniquindio.proyecto.domain.valueObject;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import java.util.UUID;

public record IdentificacionUsuario(UUID value) {
    public IdentificacionUsuario {
        if (value == null) {
            throw new DomainException("IdentificacionUsuario no puede ser null");
        }
    }

    public static IdentificacionUsuario newId() {
        return new IdentificacionUsuario(UUID.randomUUID());
    }
}