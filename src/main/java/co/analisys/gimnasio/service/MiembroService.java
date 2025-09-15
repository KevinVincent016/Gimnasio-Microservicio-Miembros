package co.analisys.gimnasio.service;

import org.springframework.stereotype.Service;
import co.analisys.gimnasio.repository.MiembroRepository;
import co.analisys.gimnasio.model.Miembro;
import java.util.List;
import co.analisys.gimnasio.model.events.MiembroInscritoEvent;
import org.springframework.beans.factory.ObjectProvider;

@Service
public class MiembroService {

    private final MiembroRepository miembroRepository;

    private final ObjectProvider<NotificationPublisher> notificationPublisherProvider;

    public MiembroService(MiembroRepository miembroRepository, ObjectProvider<NotificationPublisher> notificationPublisherProvider) {
        this.miembroRepository = miembroRepository;
        this.notificationPublisherProvider = notificationPublisherProvider;
    }

    public Miembro registrarMiembro(Miembro miembro) {
        Miembro saved = miembroRepository.save(miembro);
        // Publicar evento de inscripción
        MiembroInscritoEvent event = new MiembroInscritoEvent(
            saved.getId(), saved.getNombre(), saved.getEmail(), saved.getFechaInscripcion()
        );
        NotificationPublisher publisher = notificationPublisherProvider.getIfAvailable();
        if (publisher != null) {
            publisher.publishMemberSignup(event);
        }
        return saved;
    }

    public List<Miembro> obtenerTodosMiembros() {
        return miembroRepository.findAll();
    }

    public Miembro obtenerMiembroPorId(Long id) {
        return miembroRepository.findById(id).orElse(null);
    }

    public Miembro actualizarMiembro(Long id, Miembro miembroActualizado) {
        return miembroRepository.findById(id).map(miembro -> {
            miembro.setNombre(miembroActualizado.getNombre());
            miembro.setEmail(miembroActualizado.getEmail());
            miembro.setFechaInscripcion(miembroActualizado.getFechaInscripcion());
            return miembroRepository.save(miembro);
        }).orElse(null);
    }

    public boolean eliminarMiembro(Long id) {
        if (miembroRepository.existsById(id)) {
            miembroRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
