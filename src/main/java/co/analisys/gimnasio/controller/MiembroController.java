package co.analisys.gimnasio.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import co.analisys.gimnasio.model.Miembro;
import co.analisys.gimnasio.service.MiembroService;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/members")
public class MiembroController {

    @Autowired
    private MiembroService miembroService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public Miembro registrarMiembro(@RequestBody Miembro miembro) {
        return miembroService.registrarMiembro(miembro);
    }

    @GetMapping
    public List<Miembro> obtenerTodosMiembros() {
        return miembroService.obtenerTodosMiembros();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Miembro> obtenerMiembroPorId(@PathVariable Long id) {
        Miembro miembro = miembroService.obtenerMiembroPorId(id);
        if (miembro != null) {
            return ResponseEntity.ok(miembro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Miembro> actualizarMiembro(@PathVariable Long id, @RequestBody Miembro miembro) {
        Miembro actualizado = miembroService.actualizarMiembro(id, miembro);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminarMiembro(@PathVariable Long id) {
        boolean eliminado = miembroService.eliminarMiembro(id);
        if (eliminado) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/classes")
    public ResponseEntity<Object> obtenerClasesDeMiembro(@PathVariable Long id) {
        String url = "http://localhost:8082/api/classes?miembroId=" + id;
        Object clases = restTemplate.getForObject(url, Object.class);
        return ResponseEntity.ok(clases);
    }

}