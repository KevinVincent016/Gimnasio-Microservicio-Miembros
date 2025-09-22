package co.analisys.gimnasio.service;

import co.analisys.gimnasio.model.Pago;
import co.analisys.gimnasio.model.events.PagoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoConsumer {

    private final PagoService pagoService;
    private final PagoProcessorService pagoProcessorService;

    @Value("${app.pagos.simulation.failure-rate:0.3}")
    private double simulatedFailureRate;

    @RabbitListener(queues = "${app.pagos.queue:pagos-queue}")
    public void procesarPago(PagoEvent pagoEvent) {
        log.info("Procesando pago - ID: {}, Miembro: {}, Monto: {}, Método: {}", 
                pagoEvent.getPagoId(), pagoEvent.getMiembroId(), 
                pagoEvent.getMonto(), pagoEvent.getMetodoPago());

        try {
            // Simular procesamiento del pago
            boolean exitoso = pagoProcessorService.procesarPagoExterno(pagoEvent);
            
            if (exitoso) {
                pagoService.actualizarEstadoPago(pagoEvent.getPagoId(), 
                    Pago.EstadoPago.COMPLETADO, null);
                log.info("Pago procesado exitosamente - ID: {}", pagoEvent.getPagoId());
                
                // Enviar notificación de éxito
                enviarNotificacionExito(pagoEvent);
                
            } else {
                throw new RuntimeException("Fallo en el procesamiento del pago externo");
            }
            
        } catch (Exception e) {
            log.error("Error procesando pago ID: {} - Intento: {}", 
                    pagoEvent.getPagoId(), pagoEvent.getIntentos() + 1, e);
            
            String motivoFallo = "Error en procesamiento: " + e.getMessage();
            pagoService.actualizarEstadoPago(pagoEvent.getPagoId(), 
                Pago.EstadoPago.FALLIDO, motivoFallo);
            
            // Re-lanzar la excepción para que RabbitMQ maneje el reintento/DLQ
            throw new RuntimeException("Fallo en procesamiento de pago ID: " + pagoEvent.getPagoId(), e);
        }
    }

    @RabbitListener(queues = "${app.pagos.dlq:pagos-dlq}")
    public void procesarPagoFallido(PagoEvent pagoEvent) {
        log.error("Pago enviado a Dead Letter Queue - ID: {}, Miembro: {}, Intentos realizados: {}", 
                pagoEvent.getPagoId(), pagoEvent.getMiembroId(), pagoEvent.getIntentos());

        try {
            // Marcar definitivamente como fallido
            pagoService.actualizarEstadoPago(pagoEvent.getPagoId(), 
                Pago.EstadoPago.FALLIDO, "Máximo número de reintentos alcanzado");
            
            // Enviar notificación de fallo al miembro
            enviarNotificacionFallo(pagoEvent);
            
            // Registrar para revisión manual
            registrarParaRevisionManual(pagoEvent);
            
        } catch (Exception e) {
            log.error("Error procesando pago fallido en DLQ - ID: {}", pagoEvent.getPagoId(), e);
        }
    }

    private void enviarNotificacionExito(PagoEvent pagoEvent) {
        log.info("Enviando notificación de pago exitoso a: {} para pago ID: {}", 
                pagoEvent.getEmailMiembro(), pagoEvent.getPagoId());
        // Aquí se implementaría el envío de email/SMS de confirmación
    }

    private void enviarNotificacionFallo(PagoEvent pagoEvent) {
        log.warn("Enviando notificación de pago fallido a: {} para pago ID: {}", 
                pagoEvent.getEmailMiembro(), pagoEvent.getPagoId());
        // Aquí se implementaría el envío de email/SMS de fallo
    }

    private void registrarParaRevisionManual(PagoEvent pagoEvent) {
        log.info("Registrando pago ID: {} para revisión manual", pagoEvent.getPagoId());
        // Aquí se podría enviar a otra cola para revisión manual
        // o registrar en una tabla de pagos para revisión
    }
}