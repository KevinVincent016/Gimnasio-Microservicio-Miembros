package co.analisys.gimnasio.service;

import co.analisys.gimnasio.model.Pago;
import co.analisys.gimnasio.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoMonitoringService {

    private final PagoRepository pagoRepository;

    @Value("${app.pagos.max-retry-attempts:3}")
    private Integer maxRetryAttempts;

    @Scheduled(fixedRate = 30000) // Cada 30 segundos
    public void monitorearPagosPendientes() {
        log.info("Iniciando monitoreo de pagos pendientes...");

        List<Pago> pagosPendientes = pagoRepository.findByEstado(Pago.EstadoPago.PROCESANDO);

        for (Pago pago : pagosPendientes) {
            // Verificar pagos que llevan mucho tiempo procesando (más de 2 minutos)
            if (pago.getFechaCreacion().isBefore(LocalDateTime.now().minusMinutes(2))) {
                log.warn("Pago ID: {} lleva más de 2 minutos procesando. Estado: {}, Intentos: {}",
                        pago.getId(), pago.getEstado(), pago.getIntentos());
            }
        }

        log.info("Monitoreo completado. Pagos pendientes encontrados: {}", pagosPendientes.size());
    }

    @Scheduled(fixedRate = 60000) // Cada 1 minuto
    public void reportarEstadisticasPagos() {
        log.info("Generando estadísticas de pagos...");

        long totalPagos = pagoRepository.count();
        long pagosCompletados = pagoRepository.findByEstado(Pago.EstadoPago.COMPLETADO).size();
        long pagosFallidos = pagoRepository.findByEstado(Pago.EstadoPago.FALLIDO).size();
        long pagosPendientes = pagoRepository.findByEstado(Pago.EstadoPago.PENDIENTE).size();
        long pagosProcesando = pagoRepository.findByEstado(Pago.EstadoPago.PROCESANDO).size();

        double tasaExito = totalPagos > 0 ? (double) pagosCompletados / totalPagos * 100 : 0;
        double tasaFallo = totalPagos > 0 ? (double) pagosFallidos / totalPagos * 100 : 0;

        log.info("=== ESTADÍSTICAS DE PAGOS ===");
        log.info("Total de pagos: {}", totalPagos);
        log.info("Pagos completados: {} ({:.2f}%)", pagosCompletados, tasaExito);
        log.info("Pagos fallidos: {} ({:.2f}%)", pagosFallidos, tasaFallo);
        log.info("Pagos pendientes: {}", pagosPendientes);
        log.info("Pagos procesando: {}", pagosProcesando);
        log.info("=============================");
    }

    @Scheduled(cron = "0 */5 * * * *") // Cada 5 minutos
    public void reportePeriodico() {
        log.info("Generando reporte periódico de pagos...");

        List<Pago> pagosParaReintentar = pagoRepository.findPagosParaReintentar(maxRetryAttempts);

        if (!pagosParaReintentar.isEmpty()) {
            log.warn("Se encontraron {} pagos que podrían necesitar revisión manual:",
                    pagosParaReintentar.size());

            for (Pago pago : pagosParaReintentar) {
                log.warn("Pago ID: {}, Miembro: {}, Monto: {}, Intentos: {}, Motivo: {}",
                        pago.getId(), pago.getMiembroId(), pago.getMonto(),
                        pago.getIntentos(), pago.getMotivoFallo());
            }
        }
    }

    public void generarReporteManual() {
        log.info("Generando reporte manual de pagos...");
        reportarEstadisticasPagos();

        List<Pago> pagosFallidos = pagoRepository.findByEstado(Pago.EstadoPago.FALLIDO);
        if (!pagosFallidos.isEmpty()) {
            log.info("Detalle de pagos fallidos:");
            for (Pago pago : pagosFallidos) {
                log.info("ID: {}, Miembro: {}, Monto: {}, Método: {}, Motivo: {}",
                        pago.getId(), pago.getMiembroId(), pago.getMonto(),
                        pago.getMetodoPago(), pago.getMotivoFallo());
            }
        }
    }
}