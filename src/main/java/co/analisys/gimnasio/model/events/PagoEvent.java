package co.analisys.gimnasio.model.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoEvent {
    private Long pagoId;
    private Long miembroId;
    private BigDecimal monto;
    private String metodoPago;
    private String concepto;
    private String transactionId;
    private LocalDateTime fechaCreacion;
    private Integer intentos;
    
    // Información adicional del miembro para el procesamiento
    private String nombreMiembro;
    private String emailMiembro;
}