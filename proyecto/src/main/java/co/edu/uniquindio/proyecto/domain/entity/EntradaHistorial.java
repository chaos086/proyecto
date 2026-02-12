package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import co.edu.uniquindio.proyecto.domain.valueObject.*;

import java.time.Instant;
import java.util.UUID;

public class EntradaHistorial {
    private final UUID id;
    private final Instant fechaHora;
    private final String accion;
    private final UUID usuarioResponsableId;
    private final String observacion;

    public EntradaHistorial(UUID id, Instant fechaHora, String accion, UUID usuarioResponsableId, String observacion) {
        if (id == null) throw new DomainException("Historial.id no puede ser null");
        if (fechaHora == null) throw new DomainException("Historial.fechaHora no puede ser null");
        if (accion == null || accion.isBlank()) throw new DomainException("Historial.accion es obligatoria");
        if (usuarioResponsableId == null) throw new DomainException("Historial.usuarioResponsableId es obligatorio");
        this.id = id;
        this.fechaHora = fechaHora;
        this.accion = accion;
        this.usuarioResponsableId = usuarioResponsableId;
        this.observacion = (observacion == null) ? "" : observacion;
    }

    public UUID id() { return id; }
    public Instant fechaHora() { return fechaHora; }
    public String accion() { return accion; }
    public UUID usuarioResponsableId() { return usuarioResponsableId; }
    public String observacion() { return observacion; }
}