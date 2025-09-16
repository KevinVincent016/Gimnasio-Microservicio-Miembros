package co.analisys.gimnasio.dto.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class OcupacionClase {
    private String claseId;
    private String nombreClase;
    private int ocupacionActual;
    private int capacidadMaxima;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    // Constructores
    public OcupacionClase() {}
    
    public OcupacionClase(String claseId, String nombreClase, int ocupacionActual, int capacidadMaxima, LocalDateTime timestamp) {
        this.claseId = claseId;
        this.nombreClase = nombreClase;
        this.ocupacionActual = ocupacionActual;
        this.capacidadMaxima = capacidadMaxima;
        this.timestamp = timestamp;
    }
    
    // Getters y Setters
    public String getClaseId() { return claseId; }
    public void setClaseId(String claseId) { this.claseId = claseId; }
    
    public String getNombreClase() { return nombreClase; }
    public void setNombreClase(String nombreClase) { this.nombreClase = nombreClase; }
    
    public int getOcupacionActual() { return ocupacionActual; }
    public void setOcupacionActual(int ocupacionActual) { this.ocupacionActual = ocupacionActual; }
    
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public double getPorcentajeOcupacion() {
        return capacidadMaxima > 0 ? (double) ocupacionActual / capacidadMaxima * 100 : 0;
    }
}