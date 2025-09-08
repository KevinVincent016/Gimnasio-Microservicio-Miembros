package co.analisys.gimnasio.events;

import java.io.Serializable;

public class ClaseEvent implements Serializable {
    private Long id;
    private String nombre;
    private String tipo;        // created | updated | deleted
    private Long entrenadorId;

    public ClaseEvent() {}

    public ClaseEvent(Long id, String nombre, String tipo, Long entrenadorId) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.entrenadorId = entrenadorId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getEntrenadorId() { return entrenadorId; }
    public void setEntrenadorId(Long entrenadorId) { this.entrenadorId = entrenadorId; }

    @Override
    public String toString() {
        return "ClaseEvent{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", entrenadorId=" + entrenadorId +
                '}';
    }
}
