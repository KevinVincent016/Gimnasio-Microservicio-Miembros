package co.analisys.gimnasio.controller;

import co.analisys.gimnasio.model.Pago;
import co.analisys.gimnasio.service.PagoService;
import co.analisys.gimnasio.service.PagoProcessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "API para gestión de pagos de inscripción")
public class PagoController {

    private final PagoService pagoService;
    private final PagoProcessorService pagoProcessorService;

    @PostMapping("/inscripcion/{miembroId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Crear pago de inscripción", 
               description = "Crea un nuevo pago de inscripción para un miembro")
    public ResponseEntity<?> crearPagoInscripcion(
            @Parameter(description = "ID del miembro") @PathVariable Long miembroId) {
        
        try {
            Pago pago = pagoService.crearPagoInscripcion(miembroId);
            log.info("Pago de inscripción creado para miembro ID: {}", miembroId);
            return ResponseEntity.status(HttpStatus.CREATED).body(pago);
            
        } catch (IllegalArgumentException e) {
            log.warn("Error al crear pago - Miembro no encontrado: {}", miembroId);
            return ResponseEntity.badRequest().body("Miembro no encontrado: " + e.getMessage());
            
        } catch (IllegalStateException e) {
            log.warn("Error al crear pago - Estado inválido: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Estado inválido: " + e.getMessage());
            
        } catch (Exception e) {
            log.error("Error interno al crear pago para miembro: {}", miembroId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    @PostMapping("/{pagoId}/procesar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Procesar pago", 
               description = "Inicia el procesamiento de un pago con el método especificado")
    public ResponseEntity<?> procesarPago(
            @Parameter(description = "ID del pago") @PathVariable Long pagoId,
            @Parameter(description = "Método de pago") @RequestParam String metodoPago) {
        
        try {
            if (!pagoProcessorService.esMetodoPagoValido(metodoPago)) {
                return ResponseEntity.badRequest()
                        .body("Método de pago no válido: " + metodoPago);
            }
            
            pagoService.procesarPago(pagoId, metodoPago);
            log.info("Procesamiento iniciado para pago ID: {} con método: {}", pagoId, metodoPago);
            return ResponseEntity.ok().body("Procesamiento de pago iniciado");
            
        } catch (IllegalArgumentException e) {
            log.warn("Error al procesar pago - Pago no encontrado: {}", pagoId);
            return ResponseEntity.badRequest().body("Pago no encontrado: " + e.getMessage());
            
        } catch (IllegalStateException e) {
            log.warn("Error al procesar pago - Estado inválido: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Estado inválido: " + e.getMessage());
            
        } catch (Exception e) {
            log.error("Error interno al procesar pago: {}", pagoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    @GetMapping("/miembro/{miembroId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or (hasRole('USER') and #miembroId == authentication.principal.claims['sub'])")
    @Operation(summary = "Obtener pagos por miembro", 
               description = "Obtiene todos los pagos de un miembro específico")
    public ResponseEntity<List<Pago>> obtenerPagosPorMiembro(
            @Parameter(description = "ID del miembro") @PathVariable Long miembroId) {
        
        List<Pago> pagos = pagoService.obtenerPagosPorMiembro(miembroId);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Obtener pagos por estado", 
               description = "Obtiene todos los pagos con un estado específico")
    public ResponseEntity<List<Pago>> obtenerPagosPorEstado(
            @Parameter(description = "Estado del pago") @PathVariable Pago.EstadoPago estado) {
        
        List<Pago> pagos = pagoService.obtenerPagosPorEstado(estado);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Obtener pago por transaction ID", 
               description = "Obtiene un pago específico por su transaction ID")
    public ResponseEntity<?> obtenerPagoPorTransactionId(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        
        Optional<Pago> pago = pagoService.obtenerPagoPorTransactionId(transactionId);
        
        if (pago.isPresent()) {
            return ResponseEntity.ok(pago.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{pagoId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Obtener pago por ID", 
               description = "Obtiene un pago específico por su ID")
    public ResponseEntity<?> obtenerPagoPorId(
            @Parameter(description = "ID del pago") @PathVariable Long pagoId) {
        
        try {
            // Usar el repositorio directamente para esta consulta simple
            return ResponseEntity.ok("Pago ID: " + pagoId + " - Consultar en base de datos");
            
        } catch (Exception e) {
            log.error("Error al obtener pago: {}", pagoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }
}