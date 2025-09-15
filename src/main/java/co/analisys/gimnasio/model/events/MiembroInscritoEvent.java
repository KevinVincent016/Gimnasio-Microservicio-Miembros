package co.analisys.gimnasio.model.events;

import java.time.LocalDate;

public class MiembroInscritoEvent {
    private Long id;
    private String nombre;
    private String email;
    private LocalDate fechaInscripcion;

    public MiembroInscritoEvent() {}

    public MiembroInscritoEvent(Long id, String nombre, String email, LocalDate fechaInscripcion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.fechaInscripcion = fechaInscripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
}
