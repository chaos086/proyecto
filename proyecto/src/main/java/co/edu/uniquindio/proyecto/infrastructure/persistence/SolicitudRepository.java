package co.edu.uniquindio.proyecto.infrastructure.persistence;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.valueObject.SolicitudId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolicitudRepository {
    Solicitud save(Solicitud solicitud);
    Optional<Solicitud> findById(SolicitudId id);
    List<Solicitud> findAll();
    List<Solicitud> findBySolicitanteId(UUID solicitanteId);
    void delete(Solicitud solicitud);
    boolean existsById(SolicitudId id);
}