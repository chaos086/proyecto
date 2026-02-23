package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.BusinessRuleViolation;
import co.edu.uniquindio.proyecto.domain.valueObject.IdentificacionUsuario;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.EstadoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.Rol;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudDomainService {

    private static final int MAX_SOLICITUDES_PENDIENTES_POR_SOLICITANTE = 5;
    private static final int MAX_SOLICITUDES_EN_ATENCION_POR_DOCENTE = 10;

    public void validarCrearSolicitud(Usuario solicitante, List<Solicitud> solicitudesExistentes) {
        if (solicitante == null) {
            throw new BusinessRuleViolation("El solicitante no puede ser null");
        }
        if (!solicitante.activo()) {
            throw new BusinessRuleViolation("El solicitante debe estar activo");
        }

        long solicitudesPendientes = solicitudesExistentes.stream()
                .filter(s -> s.solicitante().value().equals(solicitante.id().value()))
                .filter(s -> s.estado() == EstadoSolicitud.REGISTRADA || 
                             s.estado() == EstadoSolicitud.CLASIFICADA ||
                             s.estado() == EstadoSolicitud.EN_ATENCION)
                .count();

        if (solicitudesPendientes >= MAX_SOLICITUDES_PENDIENTES_POR_SOLICITANTE) {
            throw new BusinessRuleViolation("Un solicitante no puede tener más de " + 
                    MAX_SOLICITUDES_PENDIENTES_POR_SOLICITANTE + " solicitudes pendientes");
        }
    }

    public void validarAsignarResponsable(Usuario responsable, List<Solicitud> solicitudesExistentes) {
        if (responsable == null) {
            throw new BusinessRuleViolation("El responsable no puede ser null");
        }
        if (!responsable.activo()) {
            throw new BusinessRuleViolation("El responsable debe estar activo");
        }
        if (responsable.rol() != Rol.DOCENTE) {
            throw new BusinessRuleViolation("Solo un docente puede ser asignado como responsable");
        }

        long solicitudesEnAtencion = solicitudesExistentes.stream()
                .filter(s -> s.responsable() != null)
                .filter(s -> s.responsable().value().equals(responsable.id().value()))
                .filter(s -> s.estado() == EstadoSolicitud.EN_ATENCION)
                .count();

        if (solicitudesEnAtencion >= MAX_SOLICITUDES_EN_ATENCION_POR_DOCENTE) {
            throw new BusinessRuleViolation("Un docente no puede tener más de " + 
                    MAX_SOLICITUDES_EN_ATENCION_POR_DOCENTE + " solicitudes en atención");
        }
    }

    public void validarTransicionEstado(EstadoSolicitud estadoActual, EstadoSolicitud estadoNuevo) {
        if (estadoActual == null || estadoNuevo == null) {
            throw new BusinessRuleViolation("Los estados no pueden ser null");
        }
    }
}