package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.infrastructure.entity.Solicitud;
import co.edu.uniquindio.proyecto.infrastructure.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.BusinessRuleViolation;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.EstadoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.Rol;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain Service que encapsula las reglas de negocio del sistema de PQRS.
 * 
 * Reglas de negocio implementadas (según Guía Maestra):
 * - Un solicitante no puede tener más de 5 solicitudes pendientes
 * - Un docente no puede tener más de 10 solicitudes en atención simultáneamente
 * - Solo usuarios activos pueden crear o atender solicitudes
 * - Solo docentes pueden ser asignados como responsables
 */
@Service
public class SolicitudDomainService {

    /** Máximo de solicitudes pendientes que puede tener un solicitante */
    private static final int MAX_SOLICITUDES_PENDIENTES_POR_SOLICITANTE = 5;
    
    /** Máximo de solicitudes en atención que puede tener un docente */
    private static final int MAX_SOLICITUDES_EN_ATENCION_POR_DOCENTE = 10;

    /**
     * Valida que un solicitante pueda crear una nueva solicitud.
     * Verifica que el usuario esté activo y no exceda el límite de solicitudes pendientes.
     * 
     * @param solicitante Usuario que desea crear la solicitud
     * @param solicitudesExistentes Lista de todas las solicitudes en el sistema
     * @throws BusinessRuleViolation si el solicitante no puede crear más solicitudes
     */
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

    /**
     * Valida que se pueda asignar un responsable a una solicitud.
     * Verifica que el responsable sea un docente activo y no exceda su límite de atención.
     * 
     * @param responsable Usuario que será asignado como responsable
     * @param solicitudesExistentes Lista de todas las solicitudes en el sistema
     * @throws BusinessRuleViolation si el responsable no puede ser asignado
     */
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

    /**
     * Valida que los estados de transición no sean nulos.
     * @param estadoActual Estado actual de la solicitud
     * @param estadoNuevo Estado al que se desea transicionar
     * @throws BusinessRuleViolation si algún estado es null
     */
    public void validarTransicionEstado(EstadoSolicitud estadoActual, EstadoSolicitud estadoNuevo) {
        if (estadoActual == null || estadoNuevo == null) {
            throw new BusinessRuleViolation("Los estados no pueden ser null");
        }
    }
}
