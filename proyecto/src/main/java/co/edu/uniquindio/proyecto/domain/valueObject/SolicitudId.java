package co.edu.uniquindio.proyecto.domain.valueObject;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import java.util.UUID;

public record SolicitudId(UUID value) {
    public SolicitudId {
        if (value == null) throw new DomainException("SolicitudId no puede ser null");
    }

    public static SolicitudId newId() {
        return new SolicitudId(UUID.randomUUID());
    }
}