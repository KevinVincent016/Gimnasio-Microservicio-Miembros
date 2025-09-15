package co.analisys.gimnasio.service;

import co.analisys.gimnasio.model.events.MiembroInscritoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "notifications.consumer.enabled", havingValue = "true", matchIfMissing = false)
public class NotificationListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @RabbitListener(queues = "${app.notifications.queue:miembros.signup.notifications}")
    public void onMemberSignup(MiembroInscritoEvent event) {
        log.info("[Demo Consumer] Nueva inscripción recibida: id={}, nombre={}, email={}, fecha={}",
                event.getId(), event.getNombre(), event.getEmail(), event.getFechaInscripcion());
    }
}
