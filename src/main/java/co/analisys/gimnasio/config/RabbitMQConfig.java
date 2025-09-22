package co.analisys.gimnasio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "notifications.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${app.notifications.exchange:miembros.notifications}")
    private String exchangeName;

    @Value("${app.notifications.queue:miembros.signup.notifications}")
    private String queueName;

    @Value("${app.notifications.routing-key:miembros.signup}")
    private String routingKey;

    // Configuración de pagos
    @Value("${app.pagos.exchange:pagos.exchange}")
    private String pagosExchangeName;

    @Value("${app.pagos.queue:pagos-queue}")
    private String pagosQueueName;

    @Value("${app.pagos.dlq:pagos-dlq}")
    private String pagosDlqName;

    @Value("${app.pagos.routing-key:pagos.procesar}")
    private String pagosRoutingKey;

    @Value("${app.pagos.dlq.routing-key:pagos.fallidos}")
    private String pagosDlqRoutingKey;

    @Value("${app.pagos.max-retry-attempts:3}")
    private Integer maxRetryAttempts;

    @Value("${app.pagos.ttl:30000}")
    private Integer messageTtl; // 30 segundos en milisegundos

    @Bean
    public TopicExchange notificationExchange() {
        log.info("Configuring RabbitMQ Exchange: {}", exchangeName);
        return new TopicExchange(exchangeName, true, false);
    }

    // Configuración del Exchange para pagos
    @Bean
    public TopicExchange pagosExchange() {
        log.info("Configuring Pagos Exchange: {}", pagosExchangeName);
        return new TopicExchange(pagosExchangeName, true, false);
    }

    @Bean
    public Queue memberSignupQueue() {
        log.info("Configuring RabbitMQ Queue: {}", queueName);
        return new Queue(queueName, true);
    }

    @Bean
    public Binding memberSignupBinding(Queue memberSignupQueue, TopicExchange notificationExchange) {
        log.info("Binding Queue '{}' to Exchange '{}' with routing key '{}'", queueName, exchangeName, routingKey);
        return BindingBuilder.bind(memberSignupQueue).to(notificationExchange).with(routingKey);
    }

    // Dead Letter Queue para pagos fallidos
    @Bean
    public Queue pagosDlq() {
        log.info("Configuring Pagos Dead Letter Queue: {}", pagosDlqName);
        return QueueBuilder.durable(pagosDlqName).build();
    }

    // Cola principal de pagos con configuración DLQ
    @Bean
    public Queue pagosQueue() {
        log.info("Configuring Pagos Queue: {} with DLQ: {}", pagosQueueName, pagosDlqName);
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", pagosExchangeName);
        args.put("x-dead-letter-routing-key", pagosDlqRoutingKey);
        args.put("x-message-ttl", messageTtl);
        args.put("x-max-retries", maxRetryAttempts);
        
        return QueueBuilder.durable(pagosQueueName)
                .withArguments(args)
                .build();
    }

    // Binding para la cola principal de pagos
    @Bean
    public Binding pagosBinding(Queue pagosQueue, TopicExchange pagosExchange) {
        log.info("Binding Pagos Queue '{}' to Exchange '{}' with routing key '{}'", 
                pagosQueueName, pagosExchangeName, pagosRoutingKey);
        return BindingBuilder.bind(pagosQueue).to(pagosExchange).with(pagosRoutingKey);
    }

    // Binding para la Dead Letter Queue
    @Bean
    public Binding pagosDlqBinding(Queue pagosDlq, TopicExchange pagosExchange) {
        log.info("Binding Pagos DLQ '{}' to Exchange '{}' with routing key '{}'", 
                pagosDlqName, pagosExchangeName, pagosDlqRoutingKey);
        return BindingBuilder.bind(pagosDlq).to(pagosExchange).with(pagosDlqRoutingKey);
    }

    @Bean
    public MessageConverter jacksonMessageConverter(org.springframework.http.converter.json.Jackson2ObjectMapperBuilder builder) {
        return new Jackson2JsonMessageConverter(builder.build());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jacksonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        return template;
    }
}
