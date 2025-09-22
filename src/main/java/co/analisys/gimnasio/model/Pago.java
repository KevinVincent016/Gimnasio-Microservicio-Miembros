package co.analisys.gimnasio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long miembroId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Column(nullable = false)
    private String metodoPago; // TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, EFECTIVO
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;
    
    @Column(nullable = false)
    private String concepto;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaProcesamiento;
    
    private String transactionId;
    
    private String motivoFallo;
    
    private Integer intentos = 0;
    
    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPago.PENDIENTE;
        }
    }
    
    public enum EstadoPago {
        PENDIENTE,
        PROCESANDO,
        COMPLETADO,
        FALLIDO,
        CANCELADO
    }
}