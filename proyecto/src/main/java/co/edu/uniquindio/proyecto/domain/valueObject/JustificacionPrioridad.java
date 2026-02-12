package co.edu.uniquindio.proyecto.domain.valueObject;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;

public record JustificacionPrioridad(String value) {
    public JustificacionPrioridad {
        if (value == null || value.isBlank())
            throw new DomainException("La justificación de prioridad es obligatoria");
        if (value.length() < 10)
            throw new DomainException("La justificación debe tener al menos 10 caracteres");
    }
}