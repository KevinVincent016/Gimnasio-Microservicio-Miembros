package co.analisys.gimnasio.service;

import co.analisys.gimnasio.model.Miembro;
import co.analisys.gimnasio.model.Pago;
import co.analisys.gimnasio.model.events.PagoEvent;
import co.analisys.gimnasio.repository.MiembroRepository;
import co.analisys.gimnasio.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final MiembroRepository miembroRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.pagos.exchange:pagos.exchange}")
    private String pagosExchange;

    @Value("${app.pagos.routing-key:pagos.procesar}")
    private String pagosRoutingKey;

    @Value("${app.pagos.monto-inscripcion:50.00}")
    private BigDecimal montoInscripcion;

    @Transactional
    public Pago crearPagoInscripcion(Long miembroId) {
        log.info("Creando pago de inscripción para miembro ID: {}", miembroId);
        
        Optional<Miembro> miembroOpt = miembroRepository.findById(miembroId);
        if (miembroOpt.isEmpty()) {
            throw new IllegalArgumentException("Miembro no encontrado con ID: " + miembroId);
        }

        Miembro miembro = miembroOpt.get();
        
        // Verificar si ya existe un pago de inscripción completado
        List<Pago> pagosExistentes = pagoRepository.findByMiembroIdAndEstado(
            miembroId, Pago.EstadoPago.COMPLETADO);
        
        boolean tieneInscripcionPagada = pagosExistentes.stream()
            .anyMatch(p -> "INSCRIPCION".equals(p.getConcepto()));
            
        if (tieneInscripcionPagada) {
            throw new IllegalStateException("El miembro ya tiene un pago de inscripción completado");
        }

        Pago pago = new Pago();
        pago.setMiembroId(miembroId);
        pago.setMonto(montoInscripcion);
        pago.setMetodoPago("PENDIENTE");
        pago.setConcepto("INSCRIPCION");
        pago.setTransactionId(UUID.randomUUID().toString());
        pago.setEstado(Pago.EstadoPago.PENDIENTE);

        Pago pagoGuardado = pagoRepository.save(pago);
        log.info("Pago creado con ID: {} para miembro: {}", pagoGuardado.getId(), miembroId);

        return pagoGuardado;
    }

    @Transactional
    public void procesarPago(Long pagoId, String metodoPago) {
        log.info("Iniciando procesamiento de pago ID: {} con método: {}", pagoId, metodoPago);
        
        Optional<Pago> pagoOpt = pagoRepository.findById(pagoId);
        if (pagoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pago no encontrado con ID: " + pagoId);
        }

        Pago pago = pagoOpt.get();
        
        if (pago.getEstado() != Pago.EstadoPago.PENDIENTE) {
            throw new IllegalStateException("El pago no está en estado PENDIENTE");
        }

        // Actualizar método de pago y estado
        pago.setMetodoPago(metodoPago);
        pago.setEstado(Pago.EstadoPago.PROCESANDO);
        pagoRepository.save(pago);

        // Enviar evento a la cola para procesamiento asíncrono
        enviarEventoPago(pago);
    }

    private void enviarEventoPago(Pago pago) {
        try {
            Optional<Miembro> miembroOpt = miembroRepository.findById(pago.getMiembroId());
            if (miembroOpt.isEmpty()) {
                log.error("No se pudo encontrar el miembro para el pago ID: {}", pago.getId());
                return;
            }

            Miembro miembro = miembroOpt.get();
            
            PagoEvent evento = new PagoEvent(
                pago.getId(),
                pago.getMiembroId(),
                pago.getMonto(),
                pago.getMetodoPago(),
                pago.getConcepto(),
                pago.getTransactionId(),
                pago.getFechaCreacion(),
                pago.getIntentos(),
                miembro.getNombre(),
                miembro.getEmail()
            );

            rabbitTemplate.convertAndSend(pagosExchange, pagosRoutingKey, evento);
            log.info("Evento de pago enviado a la cola para pago ID: {}", pago.getId());
            
        } catch (Exception e) {
            log.error("Error al enviar evento de pago para ID: {}", pago.getId(), e);
            // Marcar el pago como fallido si no se puede enviar el evento
            actualizarEstadoPago(pago.getId(), Pago.EstadoPago.FALLIDO, 
                "Error al enviar evento: " + e.getMessage());
        }
    }

    @Transactional
    public void actualizarEstadoPago(Long pagoId, Pago.EstadoPago nuevoEstado, String motivoFallo) {
        Optional<Pago> pagoOpt = pagoRepository.findById(pagoId);
        if (pagoOpt.isPresent()) {
            Pago pago = pagoOpt.get();
            pago.setEstado(nuevoEstado);
            
            if (nuevoEstado == Pago.EstadoPago.COMPLETADO) {
                pago.setFechaProcesamiento(LocalDateTime.now());
            } else if (nuevoEstado == Pago.EstadoPago.FALLIDO) {
                pago.setMotivoFallo(motivoFallo);
                pago.setIntentos(pago.getIntentos() + 1);
            }
            
            pagoRepository.save(pago);
            log.info("Estado del pago ID: {} actualizado a: {}", pagoId, nuevoEstado);
        }
    }

    public List<Pago> obtenerPagosPorMiembro(Long miembroId) {
        return pagoRepository.findByMiembroId(miembroId);
    }

    public List<Pago> obtenerPagosPorEstado(Pago.EstadoPago estado) {
        return pagoRepository.findByEstado(estado);
    }

    public Optional<Pago> obtenerPagoPorTransactionId(String transactionId) {
        return pagoRepository.findByTransactionId(transactionId);
    }
}