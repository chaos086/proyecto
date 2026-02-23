package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.BusinessRuleViolation;
import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import co.edu.uniquindio.proyecto.domain.valueObject.*;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.*;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa una solicitud (PQR) en el sistema.
 * Es la raíz del agregado de Solicitud.
 * 
 * Ciclo de vida de una solicitud:
 + 1. REGISTRADA - Cuando se crea la solicitud
 + 2. CLASIFICADA - Cuando el coordinador define el tipo
 + 3. EN_ATENCION - Cuando hay un docente responsable asignado
 + 4. ATENDIDA - Cuando el docente marca como atendida
 + 5. CERRADA - Cuando se completa el proceso
 */
public class Solicitud {

    private final SolicitudId id;

    private final UsuarioReferencia solicitante;
    private final CanalOrigen canalOrigen;
    private final Instant fechaRegistro;

    private TipoSolicitud tipoSolicitud;
    private DescripcionSolicitud descripcion;

    private Prioridad prioridad;
    private JustificacionPrioridad justificacionPrioridad;

    private EstadoSolicitud estado;
    private UsuarioReferencia responsable;

    private final List<EntradaHistorial> historial = new ArrayList<>();

    public Solicitud(SolicitudId id,
                     UsuarioReferencia solicitante,
                     CanalOrigen canalOrigen,
                     Instant fechaRegistro,
                     DescripcionSolicitud descripcion) {

        if (id == null) throw new DomainException("Solicitud.id es obligatorio");
        if (solicitante == null) throw new DomainException("Solicitud.solicitante es obligatorio");
        if (canalOrigen == null) throw new DomainException("Solicitud.canalOrigen es obligatorio");
        if (fechaRegistro == null) throw new DomainException("Solicitud.fechaRegistro es obligatorio");
        if (descripcion == null) throw new DomainException("Solicitud.descripcion es obligatoria");

        this.id = id;
        this.solicitante = solicitante;
        this.canalOrigen = canalOrigen;
        this.fechaRegistro = fechaRegistro;
        this.descripcion = descripcion;

        this.estado = EstadoSolicitud.REGISTRADA;
        registrarHistorial("REGISTRAR_SOLICITUD", solicitante, "Solicitud registrada");
    }

    /**
     * Factory method para crear una nueva solicitud.
     + solicitante Referencia del usuario que crea la solicitud
     + canalOrigen Canal por el cual se recibió la solicitud
     + descripcion Descripción de la solicitud
     + Nueva instancia de Solicitud
     */
    public static Solicitud crear(UsuarioReferencia solicitante, CanalOrigen canalOrigen, DescripcionSolicitud descripcion) {
        return new Solicitud(
                SolicitudId.newId(),
                solicitante,
                canalOrigen,
                Instant.now(),
                descripcion
        );
    }

    /**
     * Clasifica la solicitud con un tipo específico.
     * Solo aplicable cuando la solicitud está en estado REGISTRADA.
     + tipo Tipo de solicitud (QUEJA, RECLAMO, SUGERENCIA, SOLICITUD)
     + coordinador Usuario que realiza la clasificación
     */
    public void clasificar(TipoSolicitud tipo, UsuarioReferencia coordinador) {
        asegurarNoCerrada();
        if (estado != EstadoSolicitud.REGISTRADA)
            throw new BusinessRuleViolation("Solo se puede clasificar una solicitud en estado REGISTRADA");
        if (tipo == null) throw new DomainException("TipoSolicitud es obligatorio");
        if (coordinador == null) throw new DomainException("Coordinador es obligatorio");

        this.tipoSolicitud = tipo;
        this.estado = EstadoSolicitud.CLASIFICADA;
        registrarHistorial("CLASIFICAR_SOLICITUD", coordinador, "Tipo: " + tipo);
    }

    /**
     * Asigna una prioridad a la solicitud.
     * Solo aplicable cuando la solicitud está en estado CLASIFICADA.
     * prioridad Prioridad asignada (ALTA, MEDIA, BAJA)
     * justificacion Justificación de la prioridad
     * coordinador Usuario que asigna la prioridad
     */
    public void priorizar(Prioridad prioridad, JustificacionPrioridad justificacion, UsuarioReferencia coordinador) {
        asegurarNoCerrada();
        if (estado != EstadoSolicitud.CLASIFICADA)
            throw new BusinessRuleViolation("Solo se puede priorizar una solicitud en estado CLASIFICADA");
        if (prioridad == null) throw new DomainException("Prioridad es obligatoria");
        if (justificacion == null) throw new DomainException("Justificación es obligatoria");
        if (coordinador == null) throw new DomainException("Coordinador es obligatorio");

        this.prioridad = prioridad;
        this.justificacionPrioridad = justificacion;
        registrarHistorial("PRIORIZAR_SOLICITUD", coordinador, "Prioridad: " + prioridad);
    }

    /**
     * Asigna un docente como responsable de atender la solicitud.
     * Solo aplicable cuando la solicitud está en estado CLASIFICADA.
     * responsable Usuario docente responsable
     * coordinador Usuario que realiza la asignación
     */
    public void asignarResponsable(Usuario responsable, UsuarioReferencia coordinador) {
        asegurarNoCerrada();
        if (responsable == null) throw new DomainException("Responsable es obligatorio");
        if (!responsable.activo())
            throw new BusinessRuleViolation("No se puede asignar un responsable inactivo");

        if (estado != EstadoSolicitud.CLASIFICADA)
            throw new BusinessRuleViolation("Solo se puede asignar responsable en estado CLASIFICADA");

        this.responsable = new UsuarioReferencia(responsable.id().value(), responsable.nombre());
        this.estado = EstadoSolicitud.EN_ATENCION;
        registrarHistorial("ASIGNAR_RESPONSABLE", coordinador, "Responsable: " + responsable.nombre());
    }

    /**
     * Marca la solicitud como atendida por el responsable asignado.
     * Solo aplicable cuando la solicitud está en estado EN_ATENCION.
     + responsable Usuario que marca como atendida
     + observacion Observación de la atención
     */
    public void marcarAtendida(UsuarioReferencia responsable, String observacion) {
        asegurarNoCerrada();
        if (estado != EstadoSolicitud.EN_ATENCION)
            throw new BusinessRuleViolation("Solo se puede atender una solicitud en estado EN_ATENCION");
        if (this.responsable == null)
            throw new BusinessRuleViolation("No se puede atender sin responsable asignado");
        if (!this.responsable.equals(responsable))
            throw new BusinessRuleViolation("Solo el responsable asignado puede marcar como atendida");

        this.estado = EstadoSolicitud.ATENDIDA;
        registrarHistorial("MARCAR_ATENDIDA", responsable, observacion == null ? "Atendida" : observacion);
    }

    /**
     * Cierra la solicitud con una observación final.
     * Solo aplicable cuando la solicitud está en estado ATENDIDA.
     * responsable Usuario que cierra la solicitud
     * observacionCierre Observación de cierre
     */
    public void cerrar(UsuarioReferencia responsable, String observacionCierre) {
        asegurarNoCerrada();
        if (estado != EstadoSolicitud.ATENDIDA)
            throw new BusinessRuleViolation("Solo se puede cerrar una solicitud que haya sido ATENDIDA");
        if (observacionCierre == null || observacionCierre.isBlank())
            throw new BusinessRuleViolation("Para cerrar se requiere observación de cierre");

        this.estado = EstadoSolicitud.CERRADA;
        registrarHistorial("CERRAR_SOLICITUD", responsable, observacionCierre);
    }

    private void asegurarNoCerrada() {
        if (estado == EstadoSolicitud.CERRADA)
            throw new BusinessRuleViolation("Una solicitud CERRADA no puede modificarse");
    }

    private void registrarHistorial(String accion, UsuarioReferencia usuario, String observacion) {
        historial.add(new EntradaHistorial(
                UUID.randomUUID(),
                Instant.now(),
                accion,
                usuario,
                observacion
        ));
    }

    public SolicitudId id() { return id; }
    public UsuarioReferencia solicitante() { return solicitante; }
    public CanalOrigen canalOrigen() { return canalOrigen; }
    public Instant fechaRegistro() { return fechaRegistro; }
    public TipoSolicitud tipoSolicitud() { return tipoSolicitud; }
    public DescripcionSolicitud descripcion() { return descripcion; }
    public Prioridad prioridad() { return prioridad; }
    public JustificacionPrioridad justificacionPrioridad() { return justificacionPrioridad; }
    public EstadoSolicitud estado() { return estado; }
    public UsuarioReferencia responsable() { return responsable; }
    public List<EntradaHistorial> historial() { return List.copyOf(historial); }
}
