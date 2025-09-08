package co.analisys.gimnasio.config;

import co.analisys.gimnasio.messaging.MessagingNames;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    // --- Admin para declarar infraestructura ---
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true); // intenta autodeclarar al iniciar el contexto
        return admin;
    }

    @PostConstruct
    public void onStart() {
        log.info(">>> RabbitMQConfig cargado: declarar exchange/queue/bindings");
    }

    // --- Exchange tipo topic ---
    @Bean
    public TopicExchange gimnasioExchange() {
        return ExchangeBuilder.topicExchange(MessagingNames.EXCHANGE_GIMNASIO)
                .durable(true)
                .build();
    }

    // --- Cola para eventos de clases ---
    @Bean
    public Queue clasesEventsQueue() {
        return QueueBuilder.durable(MessagingNames.QUEUE_CLASES_EVENTS).build();
    }

    // --- Bindings para los 3 tipos de eventos ---
    @Bean
    public Binding claseCreatedBinding(Queue clasesEventsQueue, TopicExchange gimnasioExchange) {
        return BindingBuilder.bind(clasesEventsQueue)
                .to(gimnasioExchange)
                .with(MessagingNames.RK_CLASE_CREATED);
    }

    @Bean
    public Binding claseUpdatedBinding(Queue clasesEventsQueue, TopicExchange gimnasioExchange) {
        return BindingBuilder.bind(clasesEventsQueue)
                .to(gimnasioExchange)
                .with(MessagingNames.RK_CLASE_UPDATED);
    }

    @Bean
    public Binding claseDeletedBinding(Queue clasesEventsQueue, TopicExchange gimnasioExchange) {
        return BindingBuilder.bind(clasesEventsQueue)
                .to(gimnasioExchange)
                .with(MessagingNames.RK_CLASE_DELETED);
    }

    // --- Converter + template JSON ---
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    // --- (NUEVO) Declaración explícita al arrancar ---
    @Bean
    public CommandLineRunner declareInfra(
            AmqpAdmin admin,
            TopicExchange gimnasioExchange,
            Queue clasesEventsQueue,
            Binding claseCreatedBinding,
            Binding claseUpdatedBinding,
            Binding claseDeletedBinding
    ) {
        return args -> {
            log.info("Declarando infraestructura RabbitMQ…");
            admin.declareExchange(gimnasioExchange);
            admin.declareQueue(clasesEventsQueue);
            admin.declareBinding(claseCreatedBinding);
            admin.declareBinding(claseUpdatedBinding);
            admin.declareBinding(claseDeletedBinding);
            log.info("Infraestructura RabbitMQ declarada ✅");
        };
    }
}
