package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.BusinessRuleViolation;
import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import co.edu.uniquindio.proyecto.domain.valueObject.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Solicitud {

    private final SolicitudId id;

    private final UUID solicitanteId;
    private final CanalOrigen canalOrigen;
    private final Instant fechaRegistro;

    private TipoSolicitud tipoSolicitud;              // se define al clasificar (o desde el registro)
    private DescripcionSolicitud descripcion;

    private Prioridad prioridad;
    private JustificacionPrioridad justificacionPrioridad;

    private EstadoSolicitud estado;
    private UUID responsableId;                       // docente/coordinador asignado

    private final List<EntradaHistorial> historial = new ArrayList<>();

    public Solicitud(SolicitudId id,
                     UUID solicitanteId,
                     CanalOrigen canalOrigen,
                     Instant fechaRegistro,
                     DescripcionSolicitud descripcion) {

        if (id == null) throw new DomainException("Solicitud.id es obligatorio");
        if (solicitanteId == null) throw new DomainException("Solicitud.solicitanteId es obligatorio");
        if (canalOrigen == null) throw new DomainException("Solicitud.canalOrigen es obligatorio");
        if (fechaRegistro == null) throw new DomainException("Solicitud.fechaRegistro es obligatorio");
        if (descripcion == null) throw new DomainException("Solicitud.descripcion es obligatoria");

        this.id = id;
        this.solicitanteId = solicitanteId;
        this.canalOrigen = canalOrigen;
        this.fechaRegistro = fechaRegistro;
        this.descripcion = descripcion;

        this.estado = EstadoSolicitud.REGISTRADA;
        registrarHistorial("REGISTRAR_SOLICITUD", solicitanteId, "Solicitud registrada");
    }

    // ---------- Comportamientos del dominio ----------

    public void clasificar(TipoSolicitud tipo, UUID coordinadorId) {
        asegurarNoCerrada();
        if (estado != EstadoSolicitud.REGISTRADA)
            throw new BusinessRuleViolation("Solo se puede clasificar una solicitud en estado REGISTRADA");
        if (tipo == null) throw new DomainException("TipoSolicitud es obligatorio");

        this.tipoSolicitud = tipo;
        this.estado = EstadoSolicitud.CLASIFICADA;
        registrarHistorial("CLASIFICAR_SOLICITUD", coordinadorId, "Tipo: " + tipo);
    }

    public void priorizar(Prioridad prioridad, JustificacionPrioridad justificacion, UUID coordinadorId) {
        asegurarNoCerrada();
        if (estado != EstadoSolicitud.CLASIFICADA)
            throw new BusinessRuleViolation("Solo se puede priorizar una solicitud en estado CLASIFICADA");
        if (prioridad == null) throw new DomainException("Prioridad es obligatoria");
        if (justificacion == null) throw new DomainException("Justificación es obligatoria");

        this.prioridad = prioridad;
        this.justificacionPrioridad = justificacion;
        registrarHistorial("PRIORIZAR_SOLICITUD", coordinadorId, "Prioridad: " + prioridad);
    }

    public void asignarResponsable(Usuario responsable, UUID coordinadorId) {
        asegurarNoCerrada();
        if (responsable == null) throw new DomainException("Responsable es obligatorio");
        if (!responsable.activo())
            throw new BusinessRuleViolation("No se puede asignar un responsable inactivo");

        if (estado != EstadoSolicitud.CLASIFICADA)
            throw new BusinessRuleViolation("Solo se puede asignar responsable en estado CLASIFICADA");

        this.responsableId = responsable.id();
        this.estado = EstadoSolicitud.EN_ATENCION;
        registrarHistorial("ASIGNAR_RESPONSABLE", coordinadorId, "Responsable: " + responsable.nombre());
    }

    public void marcarAtendida(UUID responsableId, String observacion) {
        asegurarNoCerrada();
        if (estado != EstadoSolicitud.EN_ATENCION)
            throw new BusinessRuleViolation("Solo se puede atender una solicitud en estado EN_ATENCION");
        if (this.responsableId == null)
            throw new BusinessRuleViolation("No se puede atender sin responsable asignado");
        if (!this.responsableId.equals(responsableId))
            throw new BusinessRuleViolation("Solo el responsable asignado puede marcar como atendida");

        this.estado = EstadoSolicitud.ATENDIDA;
        registrarHistorial("MARCAR_ATENDIDA", responsableId, observacion == null ? "Atendida" : observacion);
    }

    public void cerrar(UUID responsableId, String observacionCierre) {
        asegurarNoCerrada();
        if (estado != EstadoSolicitud.ATENDIDA)
            throw new BusinessRuleViolation("Solo se puede cerrar una solicitud que haya sido ATENDIDA");
        if (observacionCierre == null || observacionCierre.isBlank())
            throw new BusinessRuleViolation("Para cerrar se requiere observación de cierre");

        this.estado = EstadoSolicitud.CERRADA;
        registrarHistorial("CERRAR_SOLICITUD", responsableId, observacionCierre);
    }

    private void asegurarNoCerrada() {
        if (estado == EstadoSolicitud.CERRADA)
            throw new BusinessRuleViolation("Una solicitud CERRADA no puede modificarse");
    }

    private void registrarHistorial(String accion, UUID usuarioId, String observacion) {
        historial.add(new EntradaHistorial(
                UUID.randomUUID(),
                Instant.now(),
                accion,
                usuarioId,
                observacion
        ));
    }

    // Getters de dominio
    public SolicitudId id() { return id; }
    public UUID solicitanteId() { return solicitanteId; }
    public CanalOrigen canalOrigen() { return canalOrigen; }
    public Instant fechaRegistro() { return fechaRegistro; }
    public TipoSolicitud tipoSolicitud() { return tipoSolicitud; }
    public DescripcionSolicitud descripcion() { return descripcion; }
    public Prioridad prioridad() { return prioridad; }
    public JustificacionPrioridad justificacionPrioridad() { return justificacionPrioridad; }
    public EstadoSolicitud estado() { return estado; }
    public UUID responsableId() { return responsableId; }
    public List<EntradaHistorial> historial() { return List.copyOf(historial); }
}