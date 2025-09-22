package co.analisys.gimnasio.service;

import org.springframework.stereotype.Service;
import co.analisys.gimnasio.repository.MiembroRepository;
import co.analisys.gimnasio.model.Miembro;
import co.analisys.gimnasio.model.Pago;
import java.util.List;
import co.analisys.gimnasio.model.events.MiembroInscritoEvent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MiembroService {

    private final MiembroRepository miembroRepository;
    private final ObjectProvider<NotificationPublisher> notificationPublisherProvider;
    private final ObjectProvider<PagoService> pagoServiceProvider;

    public MiembroService(MiembroRepository miembroRepository, 
                         ObjectProvider<NotificationPublisher> notificationPublisherProvider,
                         ObjectProvider<PagoService> pagoServiceProvider) {
        this.miembroRepository = miembroRepository;
        this.notificationPublisherProvider = notificationPublisherProvider;
        this.pagoServiceProvider = pagoServiceProvider;
    }

    @Transactional
    public Miembro registrarMiembro(Miembro miembro) {
        log.info("Registrando nuevo miembro: {}", miembro.getNombre());
        
        // Guardar el miembro
        Miembro saved = miembroRepository.save(miembro);
        log.info("Miembro guardado con ID: {}", saved.getId());
        
        try {
            // Crear pago de inscripción automáticamente
            PagoService pagoService = pagoServiceProvider.getIfAvailable();
            if (pagoService != null) {
                Pago pagoInscripcion = pagoService.crearPagoInscripcion(saved.getId());
                log.info("Pago de inscripción creado automáticamente - ID: {} para miembro: {}", 
                        pagoInscripcion.getId(), saved.getId());
            } else {
                log.warn("PagoService no disponible, no se pudo crear pago de inscripción para miembro: {}", 
                        saved.getId());
            }
            
            // Publicar evento de inscripción
            MiembroInscritoEvent event = new MiembroInscritoEvent(
                saved.getId(), saved.getNombre(), saved.getEmail(), saved.getFechaInscripcion()
            );
            NotificationPublisher publisher = notificationPublisherProvider.getIfAvailable();
            if (publisher != null) {
                publisher.publishMemberSignup(event);
                log.info("Evento de inscripción publicado para miembro: {}", saved.getId());
            }
            
        } catch (Exception e) {
            log.error("Error al procesar inscripción completa para miembro: {}", saved.getId(), e);
            // No re-lanzamos la excepción para no afectar el registro del miembro
            // El pago se puede crear manualmente después
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
