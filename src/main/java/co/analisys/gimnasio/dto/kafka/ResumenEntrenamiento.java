package co.analisys.gimnasio.dto.kafka;

import java.time.LocalDateTime;

public class ResumenEntrenamiento {
    private String miembroId;
    private int totalSesiones;
    private int totalMinutos;
    private int totalCalorias;
    private double promedioIntensidad;
    private LocalDateTime ultimaActualizacion;
    
    // Constructor por defecto
    public ResumenEntrenamiento() {
        this.totalSesiones = 0;
        this.totalMinutos = 0;
        this.totalCalorias = 0;
        this.promedioIntensidad = 0.0;
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    // Método para actualizar con nuevos datos
    public ResumenEntrenamiento actualizar(DatosEntrenamiento datos) {
        this.miembroId = datos.getMiembroId();
        this.totalSesiones++;
        this.totalMinutos += datos.getDuracionMinutos();
        this.totalCalorias += datos.getCaloriasQuemadas();
        
        // Calcular promedio de intensidad (BAJA=1, MEDIA=2, ALTA=3)
        int intensidadValor = obtenerValorIntensidad(datos.getIntensidad());
        this.promedioIntensidad = ((this.promedioIntensidad * (this.totalSesiones - 1)) + intensidadValor) / this.totalSesiones;
        
        this.ultimaActualizacion = LocalDateTime.now();
        return this;
    }
    
    private int obtenerValorIntensidad(String intensidad) {
        switch (intensidad.toUpperCase()) {
            case "BAJA": return 1;
            case "MEDIA": return 2;
            case "ALTA": return 3;
            default: return 1;
        }
    }
    
    // Getters y Setters
    public String getMiembroId() { return miembroId; }
    public void setMiembroId(String miembroId) { this.miembroId = miembroId; }
    
    public int getTotalSesiones() { return totalSesiones; }
    public void setTotalSesiones(int totalSesiones) { this.totalSesiones = totalSesiones; }
    
    public int getTotalMinutos() { return totalMinutos; }
    public void setTotalMinutos(int totalMinutos) { this.totalMinutos = totalMinutos; }
    
    public int getTotalCalorias() { return totalCalorias; }
    public void setTotalCalorias(int totalCalorias) { this.totalCalorias = totalCalorias; }
    
    public double getPromedioIntensidad() { return promedioIntensidad; }
    public void setPromedioIntensidad(double promedioIntensidad) { this.promedioIntensidad = promedioIntensidad; }
    
    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }
}