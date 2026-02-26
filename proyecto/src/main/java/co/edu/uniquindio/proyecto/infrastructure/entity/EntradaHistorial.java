package co.edu.uniquindio.proyecto.infrastructure.entity;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import co.edu.uniquindio.proyecto.domain.valueObject.UsuarioReferencia;

import java.time.Instant;
import java.util.UUID;

public class EntradaHistorial {
    private final UUID id;
    private final Instant fechaHora;
    private final String accion;
    private final UsuarioReferencia usuarioResponsable;
    private final String observacion;

    public EntradaHistorial(UUID id, Instant fechaHora, String accion, UsuarioReferencia usuarioResponsable, String observacion) {
        if (id == null) throw new DomainException("Historial.id no puede ser null");
        if (fechaHora == null) throw new DomainException("Historial.fechaHora no puede ser null");
        if (accion == null || accion.isBlank()) throw new DomainException("Historial.accion es obligatoria");
        if (usuarioResponsable == null) throw new DomainException("Historial.usuarioResponsable es obligatorio");
        
        this.id = id;
        this.fechaHora = fechaHora;
        this.accion = accion;
        this.usuarioResponsable = usuarioResponsable;
        this.observacion = (observacion == null) ? "" : observacion;
    }

    public UUID id() { return id; }
    public Instant fechaHora() { return fechaHora; }
    public String accion() { return accion; }
    public UsuarioReferencia usuarioResponsable() { return usuarioResponsable; }
    public String observacion() { return observacion; }
}