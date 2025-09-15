package co.analisys.gimnasio.service;

import co.analisys.gimnasio.model.events.MiembroInscritoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "notifications.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationPublisher {
    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.notifications.exchange:miembros.notifications}")
    private String exchange;

    @Value("${app.notifications.routing-key:miembros.signup}")
    private String routingKey;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishMemberSignup(MiembroInscritoEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("Published MiembroInscritoEvent to exchange='{}' routingKey='{}' for miembro id={}", exchange, routingKey, event.getId());
        } catch (Exception ex) {
            log.error("Failed to publish MiembroInscritoEvent: {}", ex.getMessage(), ex);
        }
    }
}
