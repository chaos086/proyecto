package co.edu.uniquindio.proyecto.application;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.DomainException;
import co.edu.uniquindio.proyecto.domain.service.SolicitudDomainService;
import co.edu.uniquindio.proyecto.domain.valueObject.*;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.*;
import co.edu.uniquindio.proyecto.infrastructure.persistence.SolicitudRepository;
import co.edu.uniquindio.proyecto.infrastructure.persistence.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SolicitudApplicationService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudDomainService domainService;

    public SolicitudApplicationService(
            SolicitudRepository solicitudRepository,
            UsuarioRepository usuarioRepository,
            SolicitudDomainService domainService) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.domainService = domainService;
    }

    public Solicitud crearSolicitud(UUID solicitanteId, String nombreSolicitante, 
                                     CanalOrigen canalOrigen, String descripcion) {
        if (solicitanteId == null) throw new DomainException("El ID del solicitante es obligatorio");
        if (nombreSolicitante == null || nombreSolicitante.isBlank()) {
            throw new DomainException("El nombre del solicitante es obligatorio");
        }
        if (canalOrigen == null) throw new DomainException("El canal de origen es obligatorio");
        if (descripcion == null || descripcion.isBlank()) {
            throw new DomainException("La descripción es obligatoria");
        }

        IdentificacionUsuario idSolicitante = new IdentificacionUsuario(solicitanteId);
        Usuario solicitante = usuarioRepository.findById(idSolicitante)
                .orElseThrow(() -> new DomainException("Solicitante no encontrado"));

        List<Solicitud> solicitudesExistentes = solicitudRepository.findAll();
        domainService.validarCrearSolicitud(solicitante, solicitudesExistentes);

        UsuarioReferencia solicitanteRef = new UsuarioReferencia(solicitanteId, nombreSolicitante);
        DescripcionSolicitud descripcionVO = new DescripcionSolicitud(descripcion);

        Solicitud solicitud = Solicitud.crear(solicitanteRef, canalOrigen, descripcionVO);
        
        solicitante.agregarSolicitudRegistrada(new UsuarioReferencia(
                solicitud.id().value(), 
                "Solicitud #" + solicitud.id().value().toString().substring(0, 8)
        ));
        usuarioRepository.save(solicitante);

        return solicitudRepository.save(solicitud);
    }

    public Solicitud clasificarSolicitud(UUID solicitudId, TipoSolicitud tipo, UUID coordinadorId) {
        Solicitud solicitud = obtenerSolicitud(solicitudId);
        UsuarioReferencia coordinador = obtenerReferenciaUsuario(coordinadorId);
        
        solicitud.clasificar(tipo, coordinador);
        return solicitudRepository.save(solicitud);
    }

    public Solicitud priorizarSolicitud(UUID solicitudId, Prioridad prioridad, 
                                        String justificacion, UUID coordinadorId) {
        Solicitud solicitud = obtenerSolicitud(solicitudId);
        UsuarioReferencia coordinador = obtenerReferenciaUsuario(coordinadorId);
        
        JustificacionPrioridad justificacionVO = new JustificacionPrioridad(justificacion);
        solicitud.priorizar(prioridad, justificacionVO, coordinador);
        return solicitudRepository.save(solicitud);
    }

    public Solicitud asignarResponsable(UUID solicitudId, UUID responsableId, UUID coordinadorId) {
        Solicitud solicitud = obtenerSolicitud(solicitudId);
        
        IdentificacionUsuario idResponsable = new IdentificacionUsuario(responsableId);
        Usuario responsable = usuarioRepository.findById(idResponsable)
                .orElseThrow(() -> new DomainException("Responsable no encontrado"));
        
        List<Solicitud> solicitudesExistentes = solicitudRepository.findAll();
        domainService.validarAsignarResponsable(responsable, solicitudesExistentes);
        
        UsuarioReferencia coordinador = obtenerReferenciaUsuario(coordinadorId);
        solicitud.asignarResponsable(responsable, coordinador);
        return solicitudRepository.save(solicitud);
    }

    public Solicitud marcarAtendida(UUID solicitudId, UUID responsableId, String observacion) {
        Solicitud solicitud = obtenerSolicitud(solicitudId);
        UsuarioReferencia responsable = obtenerReferenciaUsuario(responsableId);
        
        solicitud.marcarAtendida(responsable, observacion);
        return solicitudRepository.save(solicitud);
    }

    public Solicitud cerrarSolicitud(UUID solicitudId, UUID responsableId, String observacionCierre) {
        Solicitud solicitud = obtenerSolicitud(solicitudId);
        UsuarioReferencia responsable = obtenerReferenciaUsuario(responsableId);
        
        solicitud.cerrar(responsable, observacionCierre);
        return solicitudRepository.save(solicitud);
    }

    public Solicitud obtenerSolicitud(UUID solicitudId) {
        SolicitudId id = new SolicitudId(solicitudId);
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new DomainException("Solicitud no encontrada"));
    }

    public List<Solicitud> listarSolicitudes() {
        return solicitudRepository.findAll();
    }

    public List<Solicitud> listarSolicitudesPorSolicitante(UUID solicitanteId) {
        return solicitudRepository.findBySolicitanteId(solicitanteId);
    }

    private UsuarioReferencia obtenerReferenciaUsuario(UUID usuarioId) {
        IdentificacionUsuario id = new IdentificacionUsuario(usuarioId);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
        return new UsuarioReferencia(usuario.id().value(), usuario.nombre());
    }
}