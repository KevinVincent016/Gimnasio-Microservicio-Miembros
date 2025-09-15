package co.analisys.gimnasio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

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

    @Bean
    public TopicExchange notificationExchange() {
        log.info("Configuring RabbitMQ Exchange: {}", exchangeName);
        return new TopicExchange(exchangeName, true, false);
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
