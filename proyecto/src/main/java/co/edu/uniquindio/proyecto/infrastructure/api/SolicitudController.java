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

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudApplicationService solicitudService;

    public SolicitudController(SolicitudApplicationService solicitudService) {
        this.solicitudService = solicitudService;
    }

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

    @GetMapping
    public ResponseEntity<List<Solicitud>> listarSolicitudes() {
        return ResponseEntity.ok(solicitudService.listarSolicitudes());
    }

    @GetMapping("/solicitante/{solicitanteId}")
    public ResponseEntity<List<Solicitud>> listarPorSolicitante(@PathVariable UUID solicitanteId) {
        return ResponseEntity.ok(solicitudService.listarSolicitudesPorSolicitante(solicitanteId));
    }

    @PutMapping("/{id}/clasificar")
    public ResponseEntity<Solicitud> clasificar(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        String tipoStr = (String) payload.get("tipo");
        UUID coordinadorId = UUID.fromString((String) payload.get("coordinadorId"));

        TipoSolicitud tipo = TipoSolicitud.valueOf(tipoStr.toUpperCase());
        Solicitud solicitud = solicitudService.clasificarSolicitud(id, tipo, coordinadorId);
        return ResponseEntity.ok(solicitud);
    }

    @PutMapping("/{id}/priorizar")
    public ResponseEntity<Solicitud> priorizar(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        String prioridadStr = (String) payload.get("prioridad");
        String justificacion = (String) payload.get("justificacion");
        UUID coordinadorId = UUID.fromString((String) payload.get("coordinadorId"));

        Prioridad prioridad = Prioridad.valueOf(prioridadStr.toUpperCase());
        Solicitud solicitud = solicitudService.priorizarSolicitud(id, prioridad, justificacion, coordinadorId);
        return ResponseEntity.ok(solicitud);
    }

    @PutMapping("/{id}/asignar-responsable")
    public ResponseEntity<Solicitud> asignarResponsable(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        UUID responsableId = UUID.fromString((String) payload.get("responsableId"));
        UUID coordinadorId = UUID.fromString((String) payload.get("coordinadorId"));

        Solicitud solicitud = solicitudService.asignarResponsable(id, responsableId, coordinadorId);
        return ResponseEntity.ok(solicitud);
    }

    @PutMapping("/{id}/atender")
    public ResponseEntity<Solicitud> marcarAtendida(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        UUID responsableId = UUID.fromString((String) payload.get("responsableId"));
        String observacion = (String) payload.get("observacion");

        Solicitud solicitud = solicitudService.marcarAtendida(id, responsableId, observacion);
        return ResponseEntity.ok(solicitud);
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<Solicitud> cerrar(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        UUID responsableId = UUID.fromString((String) payload.get("responsableId"));
        String observacionCierre = (String) payload.get("observacionCierre");

        Solicitud solicitud = solicitudService.cerrarSolicitud(id, responsableId, observacionCierre);
        return ResponseEntity.ok(solicitud);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtenerSolicitud(@PathVariable UUID id) {
        Solicitud solicitud = solicitudService.obtenerSolicitud(id);
        return ResponseEntity.ok(solicitud);
    }
}