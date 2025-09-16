package co.analisys.gimnasio.service.kafka;

import co.analisys.gimnasio.dto.kafka.DatosEntrenamiento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DatosEntrenamientoProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(DatosEntrenamientoProducer.class);
    private static final String TOPIC = "datos-entrenamiento";
    
    @Autowired
    private KafkaTemplate<String, DatosEntrenamiento> kafkaTemplate;
    
    public void registrarEntrenamiento(String miembroId, String claseId, String entrenadorId, 
                                     String equipoId, String tipoEntrenamiento, int duracionMinutos, 
                                     int caloriasQuemadas, String intensidad) {
        try {
            DatosEntrenamiento datos = new DatosEntrenamiento(
                miembroId, claseId, entrenadorId, equipoId, 
                tipoEntrenamiento, duracionMinutos, caloriasQuemadas, 
                intensidad, LocalDateTime.now()
            );
            
            kafkaTemplate.send(TOPIC, miembroId, datos);
            logger.info("Datos de entrenamiento enviados para miembro: {}", miembroId);
            
        } catch (Exception e) {
            logger.error("Error enviando datos de entrenamiento para miembro {}: {}", miembroId, e.getMessage());
        }
    }
}