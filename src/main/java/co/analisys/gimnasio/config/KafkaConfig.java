package co.analisys.gimnasio.config;

import co.analisys.gimnasio.dto.kafka.DatosEntrenamiento;
import co.analisys.gimnasio.dto.kafka.OcupacionClase;
import co.analisys.gimnasio.dto.kafka.ResumenEntrenamiento;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    
    @Bean
    public KafkaTemplate<String, OcupacionClase> ocupacionKafkaTemplate(
            ProducerFactory<String, OcupacionClase> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
    
    @Bean
    public KafkaTemplate<String, DatosEntrenamiento> entrenamientoKafkaTemplate(
            ProducerFactory<String, DatosEntrenamiento> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
    
    @Bean
    public KafkaTemplate<String, ResumenEntrenamiento> resumenKafkaTemplate(
            ProducerFactory<String, ResumenEntrenamiento> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}