package co.edu.uniquindio.proyecto.infrastructure.api;

import co.edu.uniquindio.proyecto.application.SolicitudApplicationService;
import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.CanalOrigen;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueObject.enums.TipoSolicitud;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller para la gestión de solicitudes (PQRS).
 * Expone los endpoints de la API para el ciclo de vida completo de una solicitud.
 * 
 * Endpoints disponibles:
 * - POST /api/solicitudes - Crear nueva solicitud
 * - GET /api/solicitudes - Listar todas las solicitudes
 * - GET /api/solicitudes/{id} - Obtener solicitud por ID
 * - GET /api/solicitudes/solicitante/{id} - Listar solicitudes de un solicitante
 * - PUT /api/solicitudes/{id}/clasificar - Clasificar solicitud (coordinador)
 * - PUT /api/solicitudes/{id}/priorizar - Priorizar solicitud (coordinador)
 * - PUT /api/solicitudes/{id}/asignar-responsable - Asignar responsable (coordinador)
 * - PUT /api/solicitudes/{id}/atender - Marcar como atendida (docente)
 * - PUT /api/solicitudes/{id}/cerrar - Cerrar solicitud (docente)
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudApplicationService solicitudService;

    public SolicitudController(SolicitudApplicationService solicitudService) {
        this.solicitudService = solicitudService;
    }

    /**
     * Crea una nueva solicitud en el sistema.
     * @param payload Datos de la solicitud (solicitanteId, nombreSolicitante, canalOrigen, descripcion)
     * @return Solicitud creada
     */
    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitud(@RequestBody Map<String, Object> payload) {
        UUID solicitanteId = UUID.fromString((String) payload.get("solicitanteId"));
        String nombreSolicitante = (String) payload.get("nombreSolicitante");
        String canalStr = (String) payload.get("canalOrigen");
        String descripcion = (String) payload.get("descripcion");

        CanalOrigen canalOrigen = CanalOrigen.valueOf(canalStr.toUpperCase());

        Solicitud solicitud = solicitudService.crearSolicitud(
                solicitanteId, nombreSolicitante, canalOrigen, descripcion
        );
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Lista todas las solicitudes del sistema.
     * @return Lista de solicitudes
     */
    @GetMapping
    public ResponseEntity<List<Solicitud>> listarSolicitudes() {
        return ResponseEntity.ok(solicitudService.listarSolicitudes());
    }

    /**
     * Lista las solicitudes de un solicitante específico.
     * @param solicitanteId UUID del solicitante
     * @return Lista de solicitudes del solicitante
     */
    @GetMapping("/solicitante/{solicitanteId}")
    public ResponseEntity<List<Solicitud>> listarPorSolicitante(@PathVariable UUID solicitanteId) {
        return ResponseEntity.ok(solicitudService.listarSolicitudesPorSolicitante(solicitanteId));
    }

    /**
     * Clasifica una solicitud con un tipo específico.
     * Solo aplicable cuando la solicitud está en estado REGISTRADA.
     * @param id UUID de la solicitud
     * @param payload Datos (tipo, coordinadorId)
     * @return Solicitud actualizada
     */
    @PutMapping("/{id}/clasificar")
    public ResponseEntity<Solicitud> clasificar(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        String tipoStr = (String) payload.get("tipo");
        UUID coordinadorId = UUID.fromString((String) payload.get("coordinadorId"));

        TipoSolicitud tipo = TipoSolicitud.valueOf(tipoStr.toUpperCase());
        Solicitud solicitud = solicitudService.clasificarSolicitud(id, tipo, coordinadorId);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Asigna una prioridad a la solicitud.
     * Solo aplicable cuando la solicitud está en estado CLASIFICADA.
     * @param id UUID de la solicitud
     * @param payload Datos (prioridad, justificacion, coordinadorId)
     * @return Solicitud actualizada
     */
    @PutMapping("/{id}/priorizar")
    public ResponseEntity<Solicitud> priorizar(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        String prioridadStr = (String) payload.get("prioridad");
        String justificacion = (String) payload.get("justificacion");
        UUID coordinadorId = UUID.fromString((String) payload.get("coordinadorId"));

        Prioridad prioridad = Prioridad.valueOf(prioridadStr.toUpperCase());
        Solicitud solicitud = solicitudService.priorizarSolicitud(id, prioridad, justificacion, coordinadorId);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Asigna un docente como responsable de la solicitud.
     * Solo aplicable cuando la solicitud está en estado CLASIFICADA.
     * @param id UUID de la solicitud
     * @param payload Datos (responsableId, coordinadorId)
     * @return Solicitud actualizada
     */
    @PutMapping("/{id}/asignar-responsable")
    public ResponseEntity<Solicitud> asignarResponsable(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        UUID responsableId = UUID.fromString((String) payload.get("responsableId"));
        UUID coordinadorId = UUID.fromString((String) payload.get("coordinadorId"));

        Solicitud solicitud = solicitudService.asignarResponsable(id, responsableId, coordinadorId);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Marca la solicitud como atendida.
     * Solo aplicable cuando la solicitud está en estado EN_ATENCION.
     * @param id UUID de la solicitud
     * @param payload Datos (responsableId, observacion)
     * @return Solicitud actualizada
     */
    @PutMapping("/{id}/atender")
    public ResponseEntity<Solicitud> marcarAtendida(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        UUID responsableId = UUID.fromString((String) payload.get("responsableId"));
        String observacion = (String) payload.get("observacion");

        Solicitud solicitud = solicitudService.marcarAtendida(id, responsableId, observacion);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Cierra la solicitud con una observación final.
     * Solo aplicable cuando la solicitud está en estado ATENDIDA.
     * @param id UUID de la solicitud
     * @param payload Datos (responsableId, observacionCierre)
     * @return Solicitud actualizada
     */
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<Solicitud> cerrar(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        UUID responsableId = UUID.fromString((String) payload.get("responsableId"));
        String observacionCierre = (String) payload.get("observacionCierre");

        Solicitud solicitud = solicitudService.cerrarSolicitud(id, responsableId, observacionCierre);
        return ResponseEntity.ok(solicitud);
    }

    /**
     * Obtiene una solicitud por su identificador.
     * @param id UUID de la solicitud
     * @return Solicitud encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtenerSolicitud(@PathVariable UUID id) {
        Solicitud solicitud = solicitudService.obtenerSolicitud(id);
        return ResponseEntity.ok(solicitud);
    }
}
