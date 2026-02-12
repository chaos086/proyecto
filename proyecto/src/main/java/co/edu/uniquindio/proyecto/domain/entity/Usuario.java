package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import co.edu.uniquindio.proyecto.domain.valueObject.types.Rol;

import java.util.UUID;

public class Usuario {

    private final UUID id;
    private final String nombre;
    private final Rol rol;
    private boolean activo;

    public Usuario(UUID id, String nombre, Rol rol, boolean activo) {
        if (id == null) throw new DomainException("Usuario.id no puede ser null");
        if (nombre == null || nombre.isBlank()) throw new DomainException("Usuario.nombre es obligatorio");
        if (rol == null) throw new DomainException("Usuario.rol es obligatorio");

        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.activo = activo;
    }

    public UUID id() { return id; }
    public String nombre() { return nombre; }
    public Rol rol() { return rol; }
    public boolean activo() { return activo; }

    public void desactivar() { this.activo = false; }
    public void activar() { this.activo = true; }
}