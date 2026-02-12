package co.edu.uniquindio.proyecto.domain.valueObject;

import java.time.Instant;

public record FechaRegistro(Instant value) {

    public FechaRegistro {
        if (value == null)
            throw new IllegalArgumentException("La fecha de registro es obligatoria");
    }

    public static FechaRegistro ahora() {
        return new FechaRegistro(Instant.now());
    }
}