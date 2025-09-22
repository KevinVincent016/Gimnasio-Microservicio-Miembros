package co.analisys.gimnasio.controller;

import co.analisys.gimnasio.service.PagoMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/pagos/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoreo de Pagos", description = "API para monitoreo y estadísticas de pagos")
public class PagoMonitoringController {

    private final PagoMonitoringService pagoMonitoringService;

    @PostMapping("/reporte-manual")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generar reporte manual", 
               description = "Genera un reporte manual de estadísticas y pagos fallidos")
    public ResponseEntity<String> generarReporteManual() {
        
        try {
            pagoMonitoringService.generarReporteManual();
            log.info("Reporte manual de pagos generado exitosamente");
            return ResponseEntity.ok("Reporte generado exitosamente. Revisar logs para detalles.");
            
        } catch (Exception e) {
            log.error("Error al generar reporte manual de pagos", e);
            return ResponseEntity.status(500)
                    .body("Error al generar reporte: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Health check del módulo de pagos", 
               description = "Verifica el estado del módulo de pagos")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Módulo de pagos operativo");
    }
}