package co.analisys.gimnasio.service;

import co.analisys.gimnasio.model.events.PagoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class PagoProcessorService {

    @Value("${app.pagos.simulation.failure-rate:0.3}")
    private double simulatedFailureRate;

    @Value("${app.pagos.simulation.processing-time:1000}")
    private long simulatedProcessingTime;

    private final Random random = new Random();

    /**
     * Simula el procesamiento de un pago con un proveedor externo
     * En un entorno real, aquí se haría la llamada a la API del procesador de pagos
     */
    public boolean procesarPagoExterno(PagoEvent pagoEvent) {
        log.info("Iniciando procesamiento externo para pago ID: {}", pagoEvent.getPagoId());

        try {
            // Simular tiempo de procesamiento
            Thread.sleep(simulatedProcessingTime);

            // Simular diferentes tipos de fallo según el método de pago
            if (debeSimularFallo(pagoEvent)) {
                String tipoFallo = simularTipoFallo(pagoEvent.getMetodoPago());
                log.warn("Simulando fallo en pago ID: {} - Tipo: {}", pagoEvent.getPagoId(), tipoFallo);
                throw new RuntimeException(tipoFallo);
            }

            log.info("Pago procesado exitosamente en sistema externo - ID: {}", pagoEvent.getPagoId());
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Procesamiento interrumpido", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado en procesamiento externo", e);
        }
    }

    private boolean debeSimularFallo(PagoEvent pagoEvent) {
        // Aumentar probabilidad de fallo en reintentos para testing
        double adjustedFailureRate = simulatedFailureRate + (pagoEvent.getIntentos() * 0.1);
        return random.nextDouble() < adjustedFailureRate;
    }

    private String simularTipoFallo(String metodoPago) {
        String[] fallosTarjeta = {
                "Tarjeta declinada por el banco",
                "Fondos insuficientes",
                "Tarjeta expirada",
                "Código de seguridad inválido"
        };

        String[] fallosTransferencia = {
                "Cuenta de destino no válida",
                "Límite de transferencia excedido",
                "Servicio bancario temporalmente no disponible"
        };

        String[] fallosGenerales = {
                "Timeout en conexión con procesador",
                "Error de red temporal",
                "Servicio de pagos en mantenimiento"
        };

        String[] fallosAUsar;

        switch (metodoPago.toUpperCase()) {
            case "TARJETA_CREDITO":
            case "TARJETA_DEBITO":
                fallosAUsar = fallosTarjeta;
                break;
            case "TRANSFERENCIA":
                fallosAUsar = fallosTransferencia;
                break;
            default:
                fallosAUsar = fallosGenerales;
        }

        return fallosAUsar[random.nextInt(fallosAUsar.length)];
    }

    /**
     * Valida si un método de pago es soportado
     */
    public boolean esMetodoPagoValido(String metodoPago) {
        return metodoPago != null && (metodoPago.equals("TARJETA_CREDITO") ||
                metodoPago.equals("TARJETA_DEBITO") ||
                metodoPago.equals("TRANSFERENCIA") ||
                metodoPago.equals("EFECTIVO"));
    }
}