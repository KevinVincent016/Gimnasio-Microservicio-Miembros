package co.analisys.gimnasio.controller;

import co.analisys.gimnasio.service.kafka.DatosEntrenamientoProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka/test")
public class KafkaTestController {
    
    @Autowired
    private DatosEntrenamientoProducer entrenamientoProducer;
    
    @PostMapping("/entrenamiento")
    public ResponseEntity<String> testEntrenamiento(
            @RequestParam String miembroId,
            @RequestParam String claseId,
            @RequestParam String entrenadorId,
            @RequestParam String equipoId,
            @RequestParam String tipoEntrenamiento,
            @RequestParam int duracionMinutos,
            @RequestParam int caloriasQuemadas,
            @RequestParam String intensidad) {
        
        try {
            entrenamientoProducer.registrarEntrenamiento(
                miembroId, claseId, entrenadorId, equipoId, 
                tipoEntrenamiento, duracionMinutos, caloriasQuemadas, intensidad
            );
            return ResponseEntity.ok("Datos de entrenamiento enviados correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error enviando datos: " + e.getMessage());
        }
    }
}