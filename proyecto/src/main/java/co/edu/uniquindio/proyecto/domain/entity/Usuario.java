package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import co.edu.uniquindio.proyecto.domain.valueObject.IdentificacionUsuario;
import co.edu.uniquindio.proyecto.domain.valueObject.UsuarioReferencia;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.Rol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Usuario {

    private final IdentificacionUsuario id;
    private final String nombre;
    private final Rol rol;
    private boolean activo;

    private final List<UsuarioReferencia> solicitudesRegistradas;

    public Usuario(IdentificacionUsuario id, String nombre, Rol rol, boolean activo) {
        if (id == null) throw new DomainException("Usuario.id no puede ser null");
        if (nombre == null || nombre.isBlank()) throw new DomainException("Usuario.nombre es obligatorio");
        if (rol == null) throw new DomainException("Usuario.rol es obligatorio");

        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.activo = activo;
        this.solicitudesRegistradas = new ArrayList<>();
    }

    public static Usuario crear(String nombre, Rol rol) {
        return new Usuario(IdentificacionUsuario.newId(), nombre, rol, true);
    }

    public IdentificacionUsuario id() { return id; }
    public String nombre() { return nombre; }
    public Rol rol() { return rol; }
    public boolean activo() { return activo; }

    public void desactivar() { this.activo = false; }
    public void activar() { this.activo = true; }

    public void agregarSolicitudRegistrada(UsuarioReferencia solicitudRef) {
        if (solicitudRef == null) {
            throw new DomainException("No se puede agregar una referencia de solicitud null");
        }
        this.solicitudesRegistradas.add(solicitudRef);
    }

    public List<UsuarioReferencia> getSolicitudesRegistradas() {
        return Collections.unmodifiableList(solicitudesRegistradas);
    }

    public int cantidadSolicitudesRegistradas() {
        return solicitudesRegistradas.size();
    }
}