package co.edu.uniquindio.proyecto.domain.valueObject;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;

public record DescripcionSolicitud(String value) {
    public DescripcionSolicitud {
        if (value == null || value.isBlank())
            throw new DomainException("La descripción no puede estar vacía");
        if (value.length() < 10)
            throw new DomainException("La descripción debe tener al menos 10 caracteres");
        if (value.length() > 1000)
            throw new DomainException("La descripción no puede superar 1000 caracteres");
    }
}
