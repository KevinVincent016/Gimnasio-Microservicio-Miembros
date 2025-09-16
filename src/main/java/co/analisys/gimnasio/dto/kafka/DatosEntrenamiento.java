package co.analisys.gimnasio.dto.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class DatosEntrenamiento {
    private String miembroId;
    private String claseId;
    private String entrenadorId;
    private String equipoId;
    private String tipoEntrenamiento;
    private int duracionMinutos;
    private int caloriasQuemadas;
    private String intensidad; // BAJA, MEDIA, ALTA
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaEntrenamiento;
    
    // Constructores
    public DatosEntrenamiento() {}
    
    public DatosEntrenamiento(String miembroId, String claseId, String entrenadorId, 
                             String equipoId, String tipoEntrenamiento, int duracionMinutos, 
                             int caloriasQuemadas, String intensidad, LocalDateTime fechaEntrenamiento) {
        this.miembroId = miembroId;
        this.claseId = claseId;
        this.entrenadorId = entrenadorId;
        this.equipoId = equipoId;
        this.tipoEntrenamiento = tipoEntrenamiento;
        this.duracionMinutos = duracionMinutos;
        this.caloriasQuemadas = caloriasQuemadas;
        this.intensidad = intensidad;
        this.fechaEntrenamiento = fechaEntrenamiento;
    }
    
    // Getters y Setters
    public String getMiembroId() { return miembroId; }
    public void setMiembroId(String miembroId) { this.miembroId = miembroId; }
    
    public String getClaseId() { return claseId; }
    public void setClaseId(String claseId) { this.claseId = claseId; }
    
    public String getEntrenadorId() { return entrenadorId; }
    public void setEntrenadorId(String entrenadorId) { this.entrenadorId = entrenadorId; }
    
    public String getEquipoId() { return equipoId; }
    public void setEquipoId(String equipoId) { this.equipoId = equipoId; }
    
    public String getTipoEntrenamiento() { return tipoEntrenamiento; }
    public void setTipoEntrenamiento(String tipoEntrenamiento) { this.tipoEntrenamiento = tipoEntrenamiento; }
    
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    
    public int getCaloriasQuemadas() { return caloriasQuemadas; }
    public void setCaloriasQuemadas(int caloriasQuemadas) { this.caloriasQuemadas = caloriasQuemadas; }
    
    public String getIntensidad() { return intensidad; }
    public void setIntensidad(String intensidad) { this.intensidad = intensidad; }
    
    public LocalDateTime getFechaEntrenamiento() { return fechaEntrenamiento; }
    public void setFechaEntrenamiento(LocalDateTime fechaEntrenamiento) { this.fechaEntrenamiento = fechaEntrenamiento; }
}