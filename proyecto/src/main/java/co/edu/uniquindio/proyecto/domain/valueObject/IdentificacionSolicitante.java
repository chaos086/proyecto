package co.edu.uniquindio.proyecto.domain.valueObject;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;

public record IdentificacionSolicitante(String value) {

    public IdentificacionSolicitante {
        if (value == null || value.isBlank())
            throw new DomainException("La identificación del solicitante es obligatoria");
    }
}